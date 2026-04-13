package com.example.whizzz.domain.usecase.push

import com.example.whizzz.domain.repository.AuthRepository
import com.example.whizzz.domain.repository.FcmTokenRepository
import kotlinx.coroutines.flow.first

/**
 * Resolves the current uid, reads the device token via [DevicePushTokenReader], and persists it with [FcmTokenRepository].
 * @author udit
 */
class RegisterPushTokenUseCase(
    private val authRepository: AuthRepository,
    private val fcmTokenRepository: FcmTokenRepository,
    private val devicePushTokenReader: DevicePushTokenReader,
) {
    /**
     * No-op success when not signed in; otherwise saves the token for the active uid.
     *
     *
     * @return Success when skipped or persisted; failure from token read or save.
     * @author udit
     */
    suspend operator fun invoke(): Result<Unit> {
        val uid = authRepository.authState().first()?.uid ?: return Result.success(Unit)
        val token = devicePushTokenReader.read().getOrElse { return Result.failure(it) }
        return fcmTokenRepository.saveToken(uid, token)
    }
}
