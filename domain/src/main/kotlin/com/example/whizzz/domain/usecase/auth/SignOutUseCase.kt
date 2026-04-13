package com.example.whizzz.domain.usecase.auth

import com.example.whizzz.domain.repository.AuthRepository

/**
 * Clears the local auth session.
 * @author udit
 */
class SignOutUseCase(
    private val authRepository: AuthRepository,
) {
    /**
     * Delegates to [AuthRepository.signOut].
     *
     * @author udit
     */
    operator fun invoke() {
        authRepository.signOut()
    }
}
