package com.example.whizzz.data.mapper

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.model.ChatMessage
import com.google.firebase.database.DataSnapshot

/**
 * Maps `Chats` push nodes to [ChatMessage].
 *
 * @author udit
 */
internal fun DataSnapshot.toChatMessageOrNull(): ChatMessage? {
    val senderId = child(WhizzzStrings.Db.CHILD_SENDER_ID).getValue(String::class.java) ?: return null
    val receiverId = child(WhizzzStrings.Db.CHILD_RECEIVER_ID).getValue(String::class.java) ?: return null
    val message = child(WhizzzStrings.Db.CHILD_MESSAGE).getValue(String::class.java) ?: return null
    val timestamp = child(WhizzzStrings.Db.CHILD_TIMESTAMP).getValue(String::class.java) ?: return null
    val seen = child(WhizzzStrings.Db.CHILD_SEEN).getValue(Boolean::class.java) ?: false
    return ChatMessage(
        pushId = key,
        senderId = senderId,
        receiverId = receiverId,
        message = message,
        timestamp = timestamp,
        seen = seen,
    )
}
