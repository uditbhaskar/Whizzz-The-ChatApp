package com.example.whizzz.data.firebase

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.repository.FcmTokenRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists FCM tokens under `Tokens/{userId}`.
 *
 * @author udit
 */
@Singleton
class FcmTokenRepositoryImpl @Inject constructor(
    database: FirebaseDatabase,
) : FcmTokenRepository {

    private val tokensRef = database.reference.child(WhizzzStrings.Db.NODE_TOKENS)

    override suspend fun saveToken(userId: String, token: String): Result<Unit> = runCatching {
        val map = mapOf(WhizzzStrings.Db.CHILD_TOKEN to token)
        tokensRef.child(userId).setValue(map).await()
    }
}
