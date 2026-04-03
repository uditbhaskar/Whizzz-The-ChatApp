package com.example.whizzz.domain.usecase.chat

import com.example.whizzz.domain.repository.ChatRepository

/**
 * Sends a 1:1 chat message and updates chat list metadata.
 * @author udit
 */
class SendConversationMessageUseCase(
    private val chatRepository: ChatRepository,
) {
    /**
     *
     * @param receiverId Recipient uid.
     * @param senderId Sender uid.
     * @param message Body text.
     * @param timestamp Monotonic string used for ordering.
     * @author udit
     */
    suspend operator fun invoke(
        receiverId: String,
        senderId: String,
        message: String,
        timestamp: String,
    ): Result<Unit> = chatRepository.sendMessage(
        receiverId = receiverId,
        senderId = senderId,
        message = message,
        timestamp = timestamp,
    )
}
