package com.example.whizzz.splash

import androidx.lifecycle.ViewModel
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.usecase.auth.StreamAuthSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

/**
 * MVI-adjacent app shell [ViewModel]: holds splash visibility until the first auth snapshot is read and start
 * navigation is queued. Uses domain use cases only (Clean Architecture).
 *
 * @param streamAuthSession Hot auth session flow from the domain layer; first value picks the start destination.
 * @author udit
 */
class SplashViewModel(
    private val streamAuthSession: StreamAuthSessionUseCase,
) : ViewModel() {

    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen: StateFlow<Boolean> = _keepSplashScreen.asStateFlow()

    /**
     * Chooses home or login from the first auth emission.
     *
     * @return Route string ([WhizzzStrings.Nav.HOME] or [WhizzzStrings.Nav.LOGIN]) for the start destination.
     * @author udit
     */
    suspend fun resolveStartRoute(): String {
        val user = streamAuthSession().first()
        return if (user != null) WhizzzStrings.Nav.HOME else WhizzzStrings.Nav.LOGIN
    }

    /**
     * Allows the system splash to hide after the initial navigation is queued.
     * @author udit
     */
    fun dismissSplashScreen() {
        _keepSplashScreen.value = false
    }
}
