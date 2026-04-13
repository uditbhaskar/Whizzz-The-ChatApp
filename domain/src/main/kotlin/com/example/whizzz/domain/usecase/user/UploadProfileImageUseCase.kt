package com.example.whizzz.domain.usecase.user

import com.example.whizzz.domain.repository.UserRepository

/**
 * Application use case: upload profile photo bytes and persist the image reference.
 *
 * @author udit
 */
class UploadProfileImageUseCase(
    private val userRepository: UserRepository,
) {
    /**
     * @param jpegBytes Compressed image bytes.
     * @param fileExtension File extension for MIME (e.g. jpeg).
     * @return [Result] with persisted image reference on success.
     * @author udit
     */
    suspend operator fun invoke(jpegBytes: ByteArray, fileExtension: String): Result<String> =
        userRepository.uploadProfileImage(jpegBytes, fileExtension)
}
