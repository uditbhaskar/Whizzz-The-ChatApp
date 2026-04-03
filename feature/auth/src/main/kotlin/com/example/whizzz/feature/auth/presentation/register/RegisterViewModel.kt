package com.example.whizzz.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.text.clampUsername
import com.example.whizzz.domain.usecase.auth.SignUpUseCase
import com.example.whizzz.domain.usecase.connectivity.ObserveNetworkOnlineUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI [ViewModel] for registration: [state], [onEvent], and [effects].
 * Depends on domain use cases only (Clean Architecture).
 *
 * @param signUpUseCase Creates account through the domain layer.
 * @param observeNetworkOnline Connectivity [kotlinx.coroutines.flow.StateFlow]; blocks sign-up when offline.
 * @author udit
 */
class RegisterViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val observeNetworkOnline: ObserveNetworkOnlineUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    private val _effects = Channel<RegisterUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    /**
     * Reduces [RegisterUiEvent] into [RegisterUiState] or triggers submission.
     *
     * @param event User intent from the UI.
     * @author udit
     */
    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.UsernameChanged ->
                _state.update { it.copy(username = event.value.clampUsername(), errorMessage = null) }
            is RegisterUiEvent.EmailChanged ->
                _state.update { it.copy(email = event.value, errorMessage = null) }
            is RegisterUiEvent.PasswordChanged ->
                _state.update { it.copy(password = event.value, errorMessage = null) }
            RegisterUiEvent.Submit -> submit()
            RegisterUiEvent.ClearForm -> _state.update { RegisterUiState() }
        }
    }

    /**
     * Validates fields and connectivity, then calls [signUpUseCase].
     * @author udit
     */
    private fun submit() {
        val s = _state.value
        if (s.username.isBlank() || s.email.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.ALL_FIELDS_REQUIRED) }
            return
        }
        if (!observeNetworkOnline().value) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.OFFLINE_AUTH_SIGN_UP) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            signUpUseCase(
                username = s.username.clampUsername(),
                email = s.email.trim(),
                password = s.password,
            )
                .onSuccess {
                    _state.update { it.copy(loading = false) }
                    _effects.send(RegisterUiEffect.NavigateHome)
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            errorMessage = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.OFFLINE_AUTH_SIGN_UP,
                                genericFallback = WhizzzStrings.Errors.REGISTRATION_FAILED,
                            ),
                        )
                    }
                }
        }
    }
}
