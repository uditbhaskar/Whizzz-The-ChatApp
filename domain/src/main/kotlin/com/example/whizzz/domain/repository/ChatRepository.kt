package com.example.whizzz.domain.repository

import com.example.whizzz.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * 1:1 messaging: conversation stream, chat list partners, send, and read receipts.
 *
 * @author udit
 */
interface ChatRepository {
    /**
     * Partner user IDs with whom the current user has a `ChatList` entry.
     *
     * @param currentUserId Signed-in UID.
     *
     * @author udit
     */
    fun observeChatPartnerIds(currentUserId: String): Flow<List<String>>

    /**
     * All messages between [myUserId] and [peerUserId], ordered for UI consumption.
     *
     * @param myUserId Signed-in UID.
     * @param peerUserId Other participant UID.
     *
     * @author udit
     */
    fun observeConversation(myUserId: String, peerUserId: String): Flow<List<ChatMessage>>

    /**
     * Appends a message and maintains `ChatList` entries for both participants.
     *
     * @author udit
     */
    suspend fun sendMessage(
        receiverId: String,
        senderId: String,
        message: String,
        timestamp: String,
    ): Result<Unit>

    /**
     * Marks messages from [peerId] to [myUserId] as seen where applicable.
     *
     * @author udit
     */
    suspend fun markPeerMessagesSeen(peerId: String, myUserId: String): Result<Unit>
}
