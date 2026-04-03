package com.example.whizzz.feature.auth.presentation.register

/**
 * UI snapshot during sign-up (MVI state).
 *
 * @property username Chosen display name input.
 * @property email Account email input.
 * @property password Password input.
 * @property loading True while sign-up is in progress.
 * @property errorMessage Validation or server error text, if any.
 * @author udit
 */
data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * User actions on the registration screen (MVI events).
 * @author udit
 */
sealed interface RegisterUiEvent {
    /**
     * Username field changed.
     *
     * @property value New username text.
     * @author udit
     */
    data class UsernameChanged(val value: String) : RegisterUiEvent

    /**
     * Email field changed.
     *
     * @property value New email text.
     * @author udit
     */
    data class EmailChanged(val value: String) : RegisterUiEvent

    /**
     * Password field changed.
     *
     * @property value New password text.
     * @author udit
     */
    data class PasswordChanged(val value: String) : RegisterUiEvent

    /**
     * User submitted the registration form.
     * @author udit
     */
    data object Submit : RegisterUiEvent

    /**
     * Clears fields after successful sign-up (for example after autofill commit).
     * @author udit
     */
    data object ClearForm : RegisterUiEvent
}

/**
 * Side effects consumed by the UI layer (navigation, etc.).
 * @author udit
 */
sealed interface RegisterUiEffect {
    /**
     * Navigate to the home shell after successful registration.
     * @author udit
     */
    data object NavigateHome : RegisterUiEffect
}
