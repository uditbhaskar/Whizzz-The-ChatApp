package com.example.whizzz.domain.usecase.user

import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes a single `Users/{userId}` document.
 * @author udit
 */
class ObserveUserProfileUseCase(
    private val userRepository: UserRepository,
) {
    /**
     *
     * @param userId Profile key to watch.
     * @author udit
     */
    operator fun invoke(userId: String): Flow<User?> = userRepository.observeUser(userId)
}
