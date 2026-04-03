package com.example.whizzz.domain.usecase.chat

import com.example.whizzz.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

/**
 * Hot stream of partner user ids under `ChatList/{currentUserId}`.
 * @author udit
 */
class ObserveChatPartnerIdsUseCase(
    private val chatRepository: ChatRepository,
) {
    /**
     *
     * @param currentUserId Signed-in uid owning the chat list node.
     * @author udit
     */
    operator fun invoke(currentUserId: String): Flow<List<String>> =
        chatRepository.observeChatPartnerIds(currentUserId)
}
