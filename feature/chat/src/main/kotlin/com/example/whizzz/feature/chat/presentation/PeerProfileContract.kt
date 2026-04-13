package com.example.whizzz.feature.chat.presentation

import com.example.whizzz.domain.model.User

/**
 * UI state for the read-only peer profile screen (MVI).
 *
 *
 * @property user Observed profile, or `null` while loading / missing.
 * @property loadError Recoverable stream error, or `null`.
 * @author udit
 */
data class PeerProfileUiState(
    val user: User? = null,
    val loadError: String? = null,
)

/**
 * User intents for PeerProfileRoute.
 * @author udit
 */
sealed interface PeerProfileUiEvent {
    data object Retry : PeerProfileUiEvent
}
