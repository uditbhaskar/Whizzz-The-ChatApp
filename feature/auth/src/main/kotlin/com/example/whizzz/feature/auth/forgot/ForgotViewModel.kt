package com.example.whizzz.feature.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import com.example.whizzz.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sends Firebase password-reset email.
 *
 * @author udit
 */
@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkConnectivity: NetworkConnectivity,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotUiState())
    val state: StateFlow<ForgotUiState> = _state.asStateFlow()

    fun onEvent(event: ForgotUiEvent) {
        when (event) {
            is ForgotUiEvent.EmailChanged ->
                _state.update { it.copy(email = event.value, errorMessage = null, message = null) }
            ForgotUiEvent.Submit -> submit()
        }
    }

    private fun submit() {
        val email = _state.value.email.trim()
        if (email.isEmpty()) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.ENTER_EMAIL) }
            return
        }
        if (!networkConnectivity.isOnline.value) {
            _state.update { it.copy(errorMessage = WhizzzStrings.Errors.OFFLINE_AUTH_RESET) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null, message = null) }
            authRepository.sendPasswordReset(email)
                .onSuccess {
                    _state.update {
                        it.copy(
                            loading = false,
                            message = WhizzzStrings.Messages.RESET_EMAIL_SENT,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            errorMessage = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.OFFLINE_AUTH_RESET,
                                genericFallback = WhizzzStrings.Errors.REQUEST_FAILED,
                            ),
                        )
                    }
                }
        }
    }
}
