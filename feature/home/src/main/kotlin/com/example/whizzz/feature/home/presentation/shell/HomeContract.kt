package com.example.whizzz.feature.home.presentation.shell

import com.example.whizzz.domain.model.User

/**
 * Shell state for the home route (connectivity banner, signed-in user for the header).
 *
 * @property isOnline Whether the device is considered online for the offline strip.
 * @property currentUser Signed-in profile for the top bar (name + avatar); null while loading or signed out.
 * @author udit
 */
data class HomeUiState(
    val isOnline: Boolean = true,
    val currentUser: User? = null,
)

/**
 * Intents for the home shell (MVI).
 * @author udit
 */
sealed interface HomeUiEvent {
    data object SignOut : HomeUiEvent
    data object RegisterPushToken : HomeUiEvent
}
