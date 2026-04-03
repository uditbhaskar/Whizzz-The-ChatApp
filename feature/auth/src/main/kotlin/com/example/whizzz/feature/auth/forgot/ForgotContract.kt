package com.example.whizzz.feature.auth.forgot

/**
 * MVI contracts for password reset email.
 *
 * @author udit
 */
data class ForgotUiState(
    val email: String = "",
    val loading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null,
)

/**
 * @author udit
 */
sealed interface ForgotUiEvent {
    data class EmailChanged(val value: String) : ForgotUiEvent
    data object Submit : ForgotUiEvent
}
