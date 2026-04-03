package com.example.whizzz.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import com.example.whizzz.domain.text.clampUsername
import com.example.whizzz.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Email registration + initial profile row.
 *
 * @author udit
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkConnectivity: NetworkConnectivity,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    private val _effects = Channel<RegisterUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

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

    private fun submit() {
        val s = _state.value
        if (s.username.isBlank() || s.email.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.ALL_FIELDS_REQUIRED) }
            return
        }
        if (!networkConnectivity.isOnline.value) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.OFFLINE_AUTH_SIGN_UP) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            authRepository.signUp(
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
