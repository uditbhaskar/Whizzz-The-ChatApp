package com.example.whizzz.domain.usecase.user

import com.example.whizzz.domain.repository.UserRepository
import com.example.whizzz.domain.text.clampBio

/**
 * Application use case: update the current user’s bio.
 *
 * @author udit
 */
class UpdateBioUseCase(
    private val userRepository: UserRepository,
) {
    /**
     * @param bio New bio text.
     * @author udit
     */
    suspend operator fun invoke(bio: String): Result<Unit> =
        userRepository.updateBio(bio.clampBio())
}
