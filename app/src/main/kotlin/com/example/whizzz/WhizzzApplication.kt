package com.example.whizzz

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.whizzz.core.strings.WhizzzStrings
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry; notification channels for FCM. App Check is installed in [AppCheckInitProvider].
 *
 * @author udit
 */
@HiltAndroidApp
class WhizzzApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            WhizzzStrings.Notification.CHANNEL_ID_MESSAGES,
            WhizzzStrings.Notification.CHANNEL_NAME_MESSAGES,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }
}
