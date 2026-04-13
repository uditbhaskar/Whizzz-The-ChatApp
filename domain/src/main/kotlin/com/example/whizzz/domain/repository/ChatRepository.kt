package com.example.whizzz.domain.repository

import com.example.whizzz.domain.model.ChatMessage
import com.example.whizzz.domain.model.PagedChatListResult
import kotlinx.coroutines.flow.Flow

/**
 * Port for 1:1 messaging backed by Realtime Database in the data layer.
 * @author udit
 */
interface ChatRepository {
    /**
     * Partner user IDs with whom the current user has a `ChatList` entry.
     *
     * @param currentUserId Signed-in UID.
     * @return Cold-backed hot [Flow] of partner id list updates.
     * @author udit
     */
    fun observeChatPartnerIds(currentUserId: String): Flow<List<String>>

    /**
     * One page of `ChatList/{currentUserId}` ordered by `timestamp` (newest first in [PagedChatListResult.entries]).
     *
     * @param currentUserId Owner of the `ChatList` node.
     * @param pageSize Max conversations to return; implementations may read one extra row to set [PagedChatListResult.hasMore].
     * @param endBeforeTimestamp For the first page, `null`. For older pages, exclusive upper bound (oldest row from the prior page).
     * @param endBeforeKey `ChatList` child key (partner id) paired with [endBeforeTimestamp], or `null` with `endBeforeTimestamp`.
     * @return [Result] with [PagedChatListResult] or a failure from RTDB.
     * @author udit
     */
    suspend fun fetchChatListPage(
        currentUserId: String,
        pageSize: Int,
        endBeforeTimestamp: String?,
        endBeforeKey: String?,
    ): Result<PagedChatListResult>

    /**
     * All messages between [myUserId] and [peerUserId], ordered for UI consumption.
     *
     * @param myUserId Signed-in UID.
     * @param peerUserId Other participant UID.
     * @return Hot [Flow] of conversation rows for the pair.
     * @author udit
     */
    fun observeConversation(myUserId: String, peerUserId: String): Flow<List<ChatMessage>>

    /**
     * Appends a message and maintains `ChatList` entries for both participants.
     *
     * @param receiverId Recipient UID.
     * @param senderId Sender UID.
     * @param message Plain-text body.
     * @param timestamp Server-ordered timestamp string.
     * @return [Result] success when write completes, or failure.
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
     * @param peerId Other participant UID (message sender to mark).
     * @param myUserId Signed-in recipient UID.
     * @return [Result] success when updates finish, or failure.
     * @author udit
     */
    suspend fun markPeerMessagesSeen(peerId: String, myUserId: String): Result<Unit>
}
