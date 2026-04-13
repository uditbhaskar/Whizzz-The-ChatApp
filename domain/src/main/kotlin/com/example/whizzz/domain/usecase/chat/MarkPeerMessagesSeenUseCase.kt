package com.example.whizzz.domain.usecase.chat

import com.example.whizzz.domain.repository.ChatRepository

/**
 * Marks inbound messages from [peerId] as seen for [myUserId].
 * @author udit
 */
class MarkPeerMessagesSeenUseCase(
    private val chatRepository: ChatRepository,
) {
    /**
     * Marks peer messages seen for the pair ([peerId], [myUserId]).
     *
     * @param peerId Chat partner uid.
     * @param myUserId Signed-in uid.
     * @author udit
     */
    suspend operator fun invoke(peerId: String, myUserId: String): Result<Unit> =
        chatRepository.markPeerMessagesSeen(peerId, myUserId)
}
