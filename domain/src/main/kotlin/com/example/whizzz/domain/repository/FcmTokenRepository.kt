package com.example.whizzz.domain.repository

/**
 * Persists FCM device tokens per user for push delivery (e.g. `Tokens/{userId}`).
 *
 * @author udit
 */
interface FcmTokenRepository {
    /**
     * Saves or replaces the token for [userId].
     *
     * @param userId Firebase Auth UID.
     * @param token FCM registration token.
     *
     * @author udit
     */
    suspend fun saveToken(userId: String, token: String): Result<Unit>
}
