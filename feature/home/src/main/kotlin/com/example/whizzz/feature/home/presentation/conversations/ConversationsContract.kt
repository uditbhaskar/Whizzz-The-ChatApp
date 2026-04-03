package com.example.whizzz.feature.home.presentation.conversations

import com.example.whizzz.domain.model.User

/**
 * Immutable UI model for the Chats tab (paged conversation partners).
 *
 *
 * @property partners Resolved user rows for the current pages (newest activity first).
 * @property listError Load failure for chat list or partner resolution, or `null`.
 * @property isInitialLoading First page in flight.
 * @property isLoadingMore Older page in flight.
 * @property hasMore Additional chat-list pages may exist.
 * @property currentUserId Signed-in uid when active; `null` when signed out.
 * @author udit
 */
data class ConversationsUiState(
    val partners: List<User> = emptyList(),
    val listError: String? = null,
    val isInitialLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val currentUserId: String? = null,
)

/**
 * User intents for the Chats tab (MVI).
 * @author udit
 */
sealed interface ConversationsUiEvent {
    data object LoadMore : ConversationsUiEvent
    data object RetryList : ConversationsUiEvent
}
