package com.example.whizzz.data.firebase

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.data.mapper.toChatMessageOrNull
import com.example.whizzz.domain.model.ChatMessage
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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Realtime Database implementation of [ChatRepository] (`Chats` + `ChatList` nodes).
 *
 * @author udit
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    database: FirebaseDatabase,
) : ChatRepository {

    private val chatsRef: DatabaseReference = database.reference.child(WhizzzStrings.Db.NODE_CHATS)
    private val chatListRef: DatabaseReference = database.reference.child(WhizzzStrings.Db.NODE_CHAT_LIST)

    override fun observeChatPartnerIds(currentUserId: String): Flow<List<String>> = callbackFlow {
        val ref = chatListRef.child(currentUserId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull {
                    it.child(WhizzzStrings.Db.CHILD_ID).getValue(String::class.java)
                }
                trySend(ids)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun observeConversation(myUserId: String, peerUserId: String): Flow<List<ChatMessage>> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.toChatMessageOrNull() }
                        .filter { m ->
                            (m.senderId == myUserId && m.receiverId == peerUserId) ||
                                (m.senderId == peerUserId && m.receiverId == myUserId)
                        }
                        .sortedBy { it.timestamp.toLongOrNull() ?: 0L }
                    trySend(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            chatsRef.addValueEventListener(listener)
            awaitClose { chatsRef.removeEventListener(listener) }
        }

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

    private suspend fun ensureChatListEntry(ownerId: String, otherId: String, timestamp: String) {
        val ref = chatListRef.child(ownerId).child(otherId)
        val snap = ref.get().await()
        if (!snap.exists()) {
            ref.child(WhizzzStrings.Db.CHILD_ID).setValue(otherId).await()
            ref.child(WhizzzStrings.Db.CHILD_TIMESTAMP).setValue(timestamp).await()
        }
    }

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
