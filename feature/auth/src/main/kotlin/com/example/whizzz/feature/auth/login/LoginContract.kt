package com.example.whizzz.feature.auth.login

/**
 * MVI contracts for the login screen.
 *
 * @author udit
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * User actions on the login screen.
 *
 * @author udit
 */
sealed interface LoginUiEvent {
    data class EmailChanged(val value: String) : LoginUiEvent
    data class PasswordChanged(val value: String) : LoginUiEvent
    data object Submit : LoginUiEvent
    /** Clears email/password after a successful sign-in (e.g. after autofill commit). */
    data object ClearForm : LoginUiEvent
}

/**
 * One-shot navigation side effects.
 *
 * @author udit
 */
sealed interface LoginUiEffect {
    data object NavigateHome : LoginUiEffect
}
