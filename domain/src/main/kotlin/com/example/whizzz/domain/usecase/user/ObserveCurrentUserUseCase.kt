package com.example.whizzz.domain.usecase.user

import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Application use case: observe the signed-in user’s profile document.
 *
 * @author udit
 */
class ObserveCurrentUserUseCase(
    private val userRepository: UserRepository,
) {
    /**
     * @return Hot [Flow] of the current profile or `null` when signed out / missing.
     * @author udit
     */
    operator fun invoke(): Flow<User?> = userRepository.observeCurrentUser()
}
