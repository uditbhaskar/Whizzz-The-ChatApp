package com.example.whizzz.feature.auth.register

/**
 * MVI contracts for registration.
 *
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
 * @author udit
 */
sealed interface RegisterUiEvent {
    data class UsernameChanged(val value: String) : RegisterUiEvent
    data class EmailChanged(val value: String) : RegisterUiEvent
    data class PasswordChanged(val value: String) : RegisterUiEvent
    data object Submit : RegisterUiEvent
    /** Clears fields after successful sign-up (e.g. after autofill commit). */
    data object ClearForm : RegisterUiEvent
}

/**
 * @author udit
 */
sealed interface RegisterUiEffect {
    data object NavigateHome : RegisterUiEffect
}
