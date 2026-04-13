package com.example.whizzz.data.push

import com.example.whizzz.domain.usecase.push.DevicePushTokenReader
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * [DevicePushTokenReader] backed by Firebase Cloud Messaging.
 * @author udit
 */
class FirebasePushTokenReader(
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance(),
) : DevicePushTokenReader {

    override suspend fun read(): Result<String> = runCatching {
        messaging.token.await()
    }
}
