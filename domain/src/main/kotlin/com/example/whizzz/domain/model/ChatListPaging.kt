package com.example.whizzz.domain.model

/**
 * One row under `ChatList/{uid}/{partnerId}` used to order conversations.
 *
 * @property partnerId Other participant’s user id.
 * @property timestamp Sort key from `ChatList` (string; compared lexicographically as in RTDB).
 * @author udit
 */
data class ChatListEntry(
    val partnerId: String,
    val timestamp: String,
)

/**
 * One page of chat list rows from [com.example.whizzz.domain.repository.ChatRepository.fetchChatListPage].
 *
 * @property entries Newest-first slice for the UI.
 * @property nextPageEndBeforeTimestamp Bound for the next `endBefore` query (oldest row in this page).
 * @property nextPageEndBeforeKey Partner id key matching [nextPageEndBeforeTimestamp].
 * @property hasMore True when another page may exist.
 * @author udit
 */
data class PagedChatListResult(
    val entries: List<ChatListEntry>,
    val nextPageEndBeforeTimestamp: String?,
    val nextPageEndBeforeKey: String?,
    val hasMore: Boolean,
)
