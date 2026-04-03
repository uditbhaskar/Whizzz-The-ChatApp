package com.example.whizzz.splash

import androidx.lifecycle.ViewModel
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Resolves initial navigation target from auth state.
 * [keepSplashScreen] stays true until [dismissSplashScreen] so the system SplashScreen API
 * can match the Compose gate (no second full-screen splash).
 *
 * @author udit
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen: StateFlow<Boolean> = _keepSplashScreen.asStateFlow()

    suspend fun resolveStartRoute(): String {
        val user = authRepository.authState().first()
        return if (user != null) WhizzzStrings.Nav.HOME else WhizzzStrings.Nav.LOGIN
    }

    fun dismissSplashScreen() {
        _keepSplashScreen.value = false
    }
}
