package com.example.whizzz.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.connectivity.NetworkConnectivity
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
 * Handles email/password sign-in (MVI reducer + effects).
 *
 * @author udit
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkConnectivity: NetworkConnectivity,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _effects = Channel<LoginUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged ->
                _state.update { it.copy(email = event.value, errorMessage = null) }
            is LoginUiEvent.PasswordChanged ->
                _state.update { it.copy(password = event.value, errorMessage = null) }
            LoginUiEvent.Submit -> submit()
            LoginUiEvent.ClearForm -> _state.update { LoginUiState() }
        }
    }

    private fun submit() {
        val email = _state.value.email.trim()
        val password = _state.value.password
        if (email.isEmpty() || password.isEmpty()) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.FILL_EMAIL_PASSWORD) }
            return
        }
        if (!networkConnectivity.isOnline.value) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.OFFLINE_AUTH_SIGN_IN) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            authRepository.signIn(email, password)
                .onSuccess {
                    _state.update { it.copy(loading = false) }
                    _effects.send(LoginUiEffect.NavigateHome)
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            errorMessage = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.OFFLINE_AUTH_SIGN_IN,
                                genericFallback = WhizzzStrings.Errors.SIGN_IN_FAILED,
                            ),
                        )
                    }
                }
        }
    }
}
