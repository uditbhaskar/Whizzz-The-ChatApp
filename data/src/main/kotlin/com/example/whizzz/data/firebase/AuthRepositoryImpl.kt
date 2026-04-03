package com.example.whizzz.data.firebase

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.model.AuthUser
import com.example.whizzz.domain.repository.AuthRepository
import com.example.whizzz.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Auth implementation of [AuthRepository].
 *
 * @author udit
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : AuthRepository {

    override fun authState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { a ->
            trySend(a.currentUser?.toDomain())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseAuthThrowable(e), e))
        }

    override suspend fun signUp(username: String, email: String, password: String): Result<Unit> =
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: error(WhizzzStrings.Errors.NO_UID_AFTER_SIGNUP)
            userRepository
                .createUserProfile(
                    userId = uid,
                    username = username,
                    email = email,
                    timestamp = System.currentTimeMillis().toString(),
                    imageUrl = WhizzzStrings.Defaults.PROFILE_IMAGE,
                )
                .getOrElse { throw it }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseAuthThrowable(e), e))
        }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseAuthThrowable(e), e))
        }

    override fun signOut() {
        auth.signOut()
    }

    private fun FirebaseUser.toDomain() = AuthUser(uid = uid, email = email)
}
