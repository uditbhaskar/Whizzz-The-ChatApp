package com.example.whizzz.domain.usecase.auth

import com.example.whizzz.domain.repository.AuthRepository

/**
 * Application use case: create account and initial profile.
 *
 * @author udit
 */
class SignUpUseCase(
    private val authRepository: AuthRepository,
) {
    /**
     * @param username Public display name.
     * @param email Account email.
     * @param password Chosen password.
     * @author udit
     */
    suspend operator fun invoke(username: String, email: String, password: String): Result<Unit> =
        authRepository.signUp(username, email, password)
}
