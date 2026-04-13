package com.example.whizzz.data.repository.firebase

import com.example.whizzz.core.common.errors.UiSafeMessageException
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

/**
 * Koin-provided [AuthRepository]: Firebase email/password auth and post-sign-up profile seeding.
 * @author udit
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : AuthRepository {

    /**
     * Listens to Firebase auth state and maps the current user to [AuthUser].
     *
     *
     * @return Distinct [Flow] of session snapshots.
     * @author udit
     */
    override fun authState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { a ->
            trySend(a.currentUser?.toDomain())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    /**
     * Email/password sign-in; failures use [mapFirebaseAuthThrowable] wrapped in [UiSafeMessageException] for UI.
     *
     *
     * @param email Account email.
     * @param password Account password.
     * @return [Result] success on completion or failure with a user-facing cause.
     * @author udit
     */
    override suspend fun signIn(email: String, password: String): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(UiSafeMessageException(mapFirebaseAuthThrowable(e)).apply { initCause(e) })
        }

    /**
     * Creates the Firebase user then seeds `Users/{uid}` via [UserRepository.createUserProfile].
     *
     *
     * @param username Display name for the new profile.
     * @param email Registration email.
     * @param password Chosen password.
     * @return [Result] success when auth and profile creation succeed.
     * @author udit
     */
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
            Result.failure(UiSafeMessageException(mapFirebaseAuthThrowable(e)).apply { initCause(e) })
        }

    /**
     * Sends Firebase’s password-reset email for [email].
     *
     *
     * @param email Target inbox for the reset link.
     * @return [Result] reflecting whether the request succeeded.
     * @author udit
     */
    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(UiSafeMessageException(mapFirebaseAuthThrowable(e)).apply { initCause(e) })
        }

    /**
     * Clears the Firebase Auth session for this app instance.
     * @author udit
     */
    override fun signOut() {
        auth.signOut()
    }

    /**
     * Maps a Firebase user to the slim domain [AuthUser].
     *
     *
     * @receiver Authenticated Firebase user.
     * @return Domain identity snapshot.
     * @author udit
     */
    private fun FirebaseUser.toDomain() = AuthUser(uid = uid, email = email)
}
