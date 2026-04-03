package com.example.whizzz.feature.auth.presentation.forgot

/**
 * UI snapshot while the user requests a password-reset email (MVI state).
 *
 * @property email User-entered email address.
 * @property loading True while the reset request is in flight.
 * @property message Success message after the reset email is sent.
 * @property errorMessage Validation or recoverable error text, if any.
 * @author udit
 */
data class ForgotUiState(
    val email: String = "",
    val loading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null,
)

/**
 * User-driven events for the forgot-password screen (MVI events).
 * @author udit
 */
sealed interface ForgotUiEvent {
    /**
     * Email field text changed.
     *
     * @property value New email text from the field.
     * @author udit
     */
    data class EmailChanged(val value: String) : ForgotUiEvent

    /**
     * User tapped submit to send the reset email.
     * @author udit
     */
    data object Submit : ForgotUiEvent
}
