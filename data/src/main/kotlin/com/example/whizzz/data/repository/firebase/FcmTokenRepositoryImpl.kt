package com.example.whizzz.data.repository.firebase

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.repository.FcmTokenRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Koin-provided [FcmTokenRepository]: persists FCM registration tokens per user in Realtime Database.
 * @author udit
 */
class FcmTokenRepositoryImpl(
    database: FirebaseDatabase,
) : FcmTokenRepository {

    private val tokensRef = database.reference.child(WhizzzStrings.Db.NODE_TOKENS)

    /**
     * Writes the device token map for [userId].
     *
     *
     * @param userId Firebase Auth UID.
     * @param token Current FCM registration token.
     * @return [Result] success when the Realtime Database write completes.
     * @author udit
     */
    override suspend fun saveToken(userId: String, token: String): Result<Unit> = runCatching {
        val map = mapOf(WhizzzStrings.Db.CHILD_TOKEN to token)
        tokensRef.child(userId).setValue(map).await()
    }
}
