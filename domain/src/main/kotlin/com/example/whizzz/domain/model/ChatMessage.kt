package com.example.whizzz.domain.model

/**
 * A single 1:1 chat message in the shared `Chats` collection.
 *
 * @property pushId Realtime Database push key, if known; used for deduplication and read receipts.
 * @property senderId UID of the user who sent the message.
 * @property receiverId UID of the recipient.
 * @property message Plain-text body.
 * @property timestamp Milliseconds or string from the backend, used for ordering.
 * @property seen Whether the recipient has marked the message as seen.
 * @author udit
 */
data class ChatMessage(
    val pushId: String?,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: String,
    val seen: Boolean,
)
