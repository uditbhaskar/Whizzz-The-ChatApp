package com.example.whizzz.domain.usecase.auth

import com.example.whizzz.domain.model.AuthUser
import com.example.whizzz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Application use case: observe the signed-in session as a hot [Flow].
 * @author udit
 */
class StreamAuthSessionUseCase(
    private val authRepository: AuthRepository,
) {
    /**
     *
     * @return Emits the current [AuthUser] or `null` when signed out.
     * @author udit
     */
    operator fun invoke(): Flow<AuthUser?> = authRepository.authState()
}
