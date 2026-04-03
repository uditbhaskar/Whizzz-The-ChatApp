package com.example.whizzz.domain.repository

/**
 * Port for persisting device push tokens per user id.
 * @author udit
 */
interface FcmTokenRepository {
    /**
     * Saves or replaces the token for [userId].
     *
     * @param userId Firebase Auth UID.
     * @param token FCM registration token.
     * @return [Result] success when to write completes.
     * @author udit
     */
    suspend fun saveToken(userId: String, token: String): Result<Unit>
}
