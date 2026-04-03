package com.example.whizzz.messaging

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.whizzz.MainActivity
import com.example.whizzz.R
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.data.prefs.OpenChatStore
import com.example.whizzz.domain.repository.FcmTokenRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * FCM: refresh token storage and show data messages when not in the active thread.
 *
 * @author udit
 */
@AndroidEntryPoint
class WhizzzFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenRepository: FcmTokenRepository

    @Inject
    lateinit var openChatStore: OpenChatStore

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        runBlocking {
            fcmTokenRepository.saveToken(uid, token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val sent = data[WhizzzStrings.Fcm.SENT] ?: return
        val fromUser = data[WhizzzStrings.Fcm.USER] ?: return
        val title = data[WhizzzStrings.Fcm.TITLE].orEmpty()
        val body = data[WhizzzStrings.Fcm.BODY].orEmpty()
        val me = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (sent != me) return
        if (openChatStore.getOpenChatUserId() == fromUser) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            fromUser.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(this, WhizzzStrings.Notification.CHANNEL_ID_MESSAGES)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title.ifBlank { WhizzzStrings.Fcm.NOTIFICATION_TITLE })
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(fromUser.hashCode(), notification)
    }
}
