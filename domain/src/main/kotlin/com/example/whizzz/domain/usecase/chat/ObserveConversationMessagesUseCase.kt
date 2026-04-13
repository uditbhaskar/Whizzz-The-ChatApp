package com.example.whizzz.domain.usecase.chat

import com.example.whizzz.domain.model.ChatMessage
import com.example.whizzz.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

/**
 * Hot stream of messages between the signed-in user and a peer.
 * @author udit
 */
class ObserveConversationMessagesUseCase(
    private val chatRepository: ChatRepository,
) {
    /**
     *
     * @param myUserId Signed-in uid.
     * @param peerUserId Other participant uid.
     * @author udit
     */
    operator fun invoke(myUserId: String, peerUserId: String): Flow<List<ChatMessage>> =
        chatRepository.observeConversation(myUserId, peerUserId)
}
