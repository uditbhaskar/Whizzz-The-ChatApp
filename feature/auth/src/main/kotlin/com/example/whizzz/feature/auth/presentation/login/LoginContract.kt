package com.example.whizzz.feature.auth.presentation.login

/**
 * UI snapshot on the login screen (MVI state).
 *
 * @property email Email field value.
 * @property password Password field value.
 * @property loading True while sign-in is in progress.
 * @property errorMessage Validation or server error text, if any.
 * @author udit
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * User actions on the login screen (MVI events).
 * @author udit
 */
sealed interface LoginUiEvent {
    /**
     * Email field changed.
     *
     * @property value New email text.
     * @author udit
     */
    data class EmailChanged(val value: String) : LoginUiEvent

    /**
     * Password field changed.
     *
     * @property value New password text.
     * @author udit
     */
    data class PasswordChanged(val value: String) : LoginUiEvent

    /**
     * User submitted credentials.
     * @author udit
     */
    data object Submit : LoginUiEvent

    /**
     * Clears email and password after successful sign-in (for example after autofill).
     * @author udit
     */
    data object ClearForm : LoginUiEvent
}

/**
 * One-shot navigation side effects from successful login (MVI effects).
 * @author udit
 */
sealed interface LoginUiEffect {
    /**
     * Navigate to the main app after successful authentication.
     * @author udit
     */
    data object NavigateHome : LoginUiEffect
}
