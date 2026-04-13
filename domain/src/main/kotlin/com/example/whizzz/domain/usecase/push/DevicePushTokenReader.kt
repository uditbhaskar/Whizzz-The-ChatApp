package com.example.whizzz.domain.usecase.push

/**
 * Abstraction for reading the device FCM registration token without pulling Firebase SDK into presentation.
 * @author udit
 */
fun interface DevicePushTokenReader {

    /**
     *
     * @return Current token string, or a failure if unavailable.
     * @author udit
     */
    suspend fun read(): Result<String>
}
