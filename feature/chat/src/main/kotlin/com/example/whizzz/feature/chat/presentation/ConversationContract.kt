package com.example.whizzz.feature.chat.presentation

import com.example.whizzz.domain.model.ChatMessage
import com.example.whizzz.domain.model.User

/**
 * Single UI snapshot for the 1:1 conversation screen (MVI).
 *
 *
 * @property myUserId Signed-in uid when known.
 * @property messages Thread rows for the pair.
 * @property peerUser Peer profile stream value.
 * @property draft Composer text.
 * @property streamError Listener / observe failure banner, or `null`.
 * @property sendError Last send failure, or `null`.
 * @author udit
 */
data class ConversationUiState(
    val myUserId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val peerUser: User? = null,
    val draft: String = "",
    val streamError: String? = null,
    val sendError: String? = null,
)

/**
 * User and lifecycle intents for ConversationRoute.
 * @author udit
 */
sealed interface ConversationUiEvent {
    data class DraftChanged(val value: String) : ConversationUiEvent
    data object Send : ConversationUiEvent
    data object DismissStreamError : ConversationUiEvent
    data object RetryStreams : ConversationUiEvent
    data object ClearSendError : ConversationUiEvent
    data object MarkPeerSeen : ConversationUiEvent
}
