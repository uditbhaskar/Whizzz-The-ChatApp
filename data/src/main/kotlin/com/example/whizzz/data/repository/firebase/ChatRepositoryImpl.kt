package com.example.whizzz.data.repository.firebase

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.data.mapper.toChatMessageOrNull
import com.example.whizzz.domain.model.ChatListEntry
import com.example.whizzz.domain.model.ChatMessage
import com.example.whizzz.domain.model.PagedChatListResult
import com.example.whizzz.domain.repository.ChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Koin-provided [ChatRepository] implementation: listens and writes `Chats` and `ChatList` in Realtime Database.
 * @author udit
 */
class ChatRepositoryImpl(
    database: FirebaseDatabase,
) : ChatRepository {

    private val chatsRef: DatabaseReference = database.reference.child(WhizzzStrings.Db.NODE_CHATS)
    private val chatListRef: DatabaseReference = database.reference.child(WhizzzStrings.Db.NODE_CHAT_LIST)

    /**
     * Streams partner ids from `ChatList/{currentUserId}` child `id` fields.
     *
     *
     * @param currentUserId Signed-in user key.
     * @return Hot [Flow] of partner UID lists.
     * @author udit
     */
    override fun observeChatPartnerIds(currentUserId: String): Flow<List<String>> = callbackFlow {
        val ref = chatListRef.child(currentUserId)
        val listener = object : ValueEventListener {
            /**
             * Emits mapped partner ids whenever the chat list snapshot updates.
             *
             *
             * @param snapshot `ChatList/{uid}` node data.
             * @author udit
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull {
                    it.child(WhizzzStrings.Db.CHILD_ID).getValue(String::class.java)
                }
                trySend(ids)
            }

            /**
             * Closes the flow with a failure when the listener is canceled.
             *
             *
             * @param error Database error details.
             * @author udit
             */
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /**
     * Reads one page of `ChatList/{currentUserId}` via `orderByChild(timestamp)` with `limitToLast` / `endBefore`.
     *
     * Requires `.indexOn": ["timestamp"]` on `ChatList/{uid}` in Realtime Database rules for the query shape used here.
     * Returns entries **newest-first** for the conversations list UI.
     *
     *
     * @param currentUserId Signed-in user; reads `ChatList/{currentUserId}`.
     * @param pageSize Positive page size; internally requests `pageSize + 1` to detect [PagedChatListResult.hasMore].
     * @param endBeforeTimestamp `endBefore` bound timestamp, or `null` for the newest page.
     * @param endBeforeKey `endBefore` tie-break key (partner id), or `null` with `endBeforeTimestamp`.
     * @return [PagedChatListResult] with cursor pair for the next older page when [PagedChatListResult.hasMore].
     * @author udit
     */
    override suspend fun fetchChatListPage(
        currentUserId: String,
        pageSize: Int,
        endBeforeTimestamp: String?,
        endBeforeKey: String?,
    ): Result<PagedChatListResult> = runCatching {
        require(pageSize > 0) { "pageSize must be positive" }
        val limit = pageSize + 1
        val base = chatListRef.child(currentUserId).orderByChild(WhizzzStrings.Db.CHILD_TIMESTAMP)
        val q = if (endBeforeTimestamp != null && endBeforeKey != null) {
            base.endBefore(endBeforeTimestamp, endBeforeKey).limitToLast(limit)
        } else {
            base.limitToLast(limit)
        }
        val snap = q.get().await()
        val rows = snap.children.mapNotNull { child ->
            val partnerId = child.key ?: return@mapNotNull null
            val idField = child.child(WhizzzStrings.Db.CHILD_ID).getValue(String::class.java) ?: partnerId
            val ts = child.child(WhizzzStrings.Db.CHILD_TIMESTAMP).getValue(String::class.java) ?: "0"
            ChatListEntry(partnerId = idField, timestamp = ts)
        }.sortedWith(compareBy({ it.timestamp }, { it.partnerId }))
        val hasMore = rows.size > pageSize
        val pageAsc = if (hasMore) rows.drop(1) else rows
        val newestFirst = pageAsc.asReversed()
        val nextPair = if (hasMore && pageAsc.isNotEmpty()) {
            val oldestDisplayed = pageAsc.first()
            oldestDisplayed.timestamp to oldestDisplayed.partnerId
        } else {
            null
        }
        PagedChatListResult(
            entries = newestFirst,
            nextPageEndBeforeTimestamp = nextPair?.first,
            nextPageEndBeforeKey = nextPair?.second,
            hasMore = hasMore,
        )
    }

    /**
     * Observes all `Chats` children, filters to the pair ([myUserId], [peerUserId]), and sorts by timestamp.
     *
     *
     * @param myUserId Signed-in UID.
     * @param peerUserId Other participant UID.
     * @return Hot [Flow] of messages for that thread.
     * @author udit
     */
    override fun observeConversation(myUserId: String, peerUserId: String): Flow<List<ChatMessage>> =
        callbackFlow {
            val listener = object : ValueEventListener {
                /**
                 * Filters global chat snapshots down to the 1:1 thread and emits ordered rows.
                 *
                 *
                 * @param snapshot Root `Chats` collection snapshot.
                 * @author udit
                 */
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.toChatMessageOrNull() }
                        .filter { m ->
                            (m.senderId == myUserId && m.receiverId == peerUserId) ||
                                (m.senderId == peerUserId && m.receiverId == myUserId)
                        }
                        .sortedBy { it.timestamp.toLongOrNull() ?: 0L }
                    trySend(list)
                }

                /**
                 * Closes the flow when the chats listener fails.
                 *
                 *
                 * @param error Database error details.
                 * @author udit
                 */
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            chatsRef.addValueEventListener(listener)
            awaitClose { chatsRef.removeEventListener(listener) }
        }

    /**
     * Pushes a new chat row and ensures symmetric `ChatList` entries when missing.
     *
     *
     * @param receiverId Recipient UID.
     * @param senderId Sender UID.
     * @param message Message body.
     * @param timestamp Ordering timestamp string.
     * @return [Result] success when writes complete.
     * @author udit
     */
    override suspend fun sendMessage(
        receiverId: String,
        senderId: String,
        message: String,
        timestamp: String,
    ): Result<Unit> = runCatching {
        val map = mapOf(
            WhizzzStrings.Db.CHILD_RECEIVER_ID to receiverId,
            WhizzzStrings.Db.CHILD_SENDER_ID to senderId,
            WhizzzStrings.Db.CHILD_MESSAGE to message,
            WhizzzStrings.Db.CHILD_TIMESTAMP to timestamp,
            WhizzzStrings.Db.CHILD_SEEN to false,
        )
        chatsRef.push().setValue(map).await()
        ensureChatListEntry(senderId, receiverId, timestamp)
        ensureChatListEntry(receiverId, senderId, timestamp)
    }

    /**
     * Creates `ChatList/{ownerId}/{otherId}` with id + timestamp when absent.
     *
     *
     * @param ownerId Chat list owner UID.
     * @param otherId Partner UID listed under the owner.
     * @param timestamp Initial list timestamp when creating the row.
     * @author udit
     */
    private suspend fun ensureChatListEntry(ownerId: String, otherId: String, timestamp: String) {
        val ref = chatListRef.child(ownerId).child(otherId)
        val snap = ref.get().await()
        if (!snap.exists()) {
            ref.child(WhizzzStrings.Db.CHILD_ID).setValue(otherId).await()
            ref.child(WhizzzStrings.Db.CHILD_TIMESTAMP).setValue(timestamp).await()
        }
    }

    /**
     * Scans `Chats` for messages sent by [peerId] to [myUserId] and sets `seen` to true.
     *
     *
     * @param peerId Sender UID to mark as read from the recipient’s perspective.
     * @param myUserId Signed-in recipient UID.
     * @return [Result] success when iteration completes.
     * @author udit
     */
    override suspend fun markPeerMessagesSeen(peerId: String, myUserId: String): Result<Unit> =
        runCatching {
            val snap = chatsRef.get().await()
            for (child in snap.children) {
                val msg = child.toChatMessageOrNull() ?: continue
                if (msg.senderId == peerId && msg.receiverId == myUserId && !msg.seen) {
                    child.ref.child(WhizzzStrings.Db.CHILD_SEEN).setValue(true).await()
                }
            }
        }
}
