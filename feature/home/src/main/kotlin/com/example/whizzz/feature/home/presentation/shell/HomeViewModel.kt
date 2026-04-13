package com.example.whizzz.feature.home.presentation.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.domain.usecase.connectivity.ObserveNetworkOnlineUseCase
import com.example.whizzz.domain.usecase.push.RegisterPushTokenUseCase
import com.example.whizzz.domain.usecase.auth.SignOutUseCase
import com.example.whizzz.domain.usecase.user.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * MVI shell [androidx.lifecycle.ViewModel]: [state] reflects connectivity; [onEvent] runs use cases.
 * @author udit
 */
class HomeViewModel(
    observeNetworkOnline: ObserveNetworkOnlineUseCase,
    observeCurrentUser: ObserveCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val registerPushTokenUseCase: RegisterPushTokenUseCase,
) : ViewModel() {

    /**
     * Connectivity and current user for the shell (header + offline strip).
     * @author udit
     */
    val state: StateFlow<HomeUiState> = combine(
        observeNetworkOnline(),
        observeCurrentUser(),
    ) { online, user ->
        HomeUiState(isOnline = online, currentUser = user)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    /**
     *
     * @param event Shell intent.
     * @author udit
     */
    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.SignOut -> signOutUseCase()
            HomeUiEvent.RegisterPushToken -> {
                viewModelScope.launch { registerPushTokenUseCase() }
            }
        }
    }
}
