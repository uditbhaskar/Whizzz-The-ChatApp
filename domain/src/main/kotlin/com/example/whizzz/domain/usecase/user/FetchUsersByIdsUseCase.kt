package com.example.whizzz.domain.usecase.user

import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.repository.UserRepository

/**
 * Batch-loads user profiles preserving first-seen order of ids.
 * @author udit
 */
class FetchUsersByIdsUseCase(
    private val userRepository: UserRepository,
) {
    /**
     *
     * @param ids Partner uids from a chat list page.
     * @author udit
     */
    suspend operator fun invoke(ids: List<String>): Result<List<User>> =
        userRepository.fetchUsersByIds(ids)
}
