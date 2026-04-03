package com.example.whizzz.domain.repository

import com.example.whizzz.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Contract for sign-in, registration, password recovery, and observing authentication state.
 *
 * Implementations live in the data layer (e.g. Firebase Auth) and must not leak SDK types here.
 *
 * @author udit
 */
interface AuthRepository {
    /**
     * Hot stream of the current session; emits `null` when signed out.
     *
     * @author udit
     */
    fun authState(): Flow<AuthUser?>

    /**
     * Signs in with email and password.
     *
     * @param email Account email.
     * @param password User password.
     * @return [Result] success with no value, or failure with the underlying error.
     *
     * @author udit
     */
    suspend fun signIn(email: String, password: String): Result<Unit>

    /**
     * Creates a new account and the corresponding user profile in persistence.
     *
     * @param username Public display name.
     * @param email Account email.
     * @param password Chosen password.
     * @return [Result] success when auth + profile creation complete, or failure otherwise.
     *
     * @author udit
     */
    suspend fun signUp(username: String, email: String, password: String): Result<Unit>

    /**
     * Triggers a password-reset email for [email].
     *
     * @param email Address to send the reset link to.
     * @return [Result] reflecting whether the request was accepted.
     *
     * @author udit
     */
    suspend fun sendPasswordReset(email: String): Result<Unit>

    /**
     * Clears the local session (sign out).
     *
     * @author udit
     */
    fun signOut()
}
