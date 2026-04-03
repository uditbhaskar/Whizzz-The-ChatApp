package com.example.whizzz.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import com.example.whizzz.domain.repository.AuthRepository
import com.example.whizzz.domain.repository.FcmTokenRepository
import com.example.whizzz.domain.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Shell: sign-out, FCM token sync, presence helpers.
 *
 * @author udit
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val fcmTokenRepository: FcmTokenRepository,
    networkConnectivity: NetworkConnectivity,
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkConnectivity.isOnline

    fun signOut() {
        authRepository.signOut()
    }

    /**
     * Registers the device FCM token for the signed-in user.
     *
     * @author udit
     */
    fun registerPushToken() {
        viewModelScope.launch {
            val uid = authRepository.authState().first()?.uid ?: return@launch
            runCatching {
                val token = FirebaseMessaging.getInstance().token.await()
                fcmTokenRepository.saveToken(uid, token)
            }
        }
    }

    /**
     * @author udit
     */
    fun setPresenceOnline() {
        viewModelScope.launch {
            userRepository.setPresence(WhizzzStrings.Defaults.PRESENCE_ONLINE)
        }
    }

    /**
     * @author udit
     */
    fun setPresenceOffline() {
        viewModelScope.launch {
            userRepository.setPresence(WhizzzStrings.Defaults.STATUS_OFFLINE)
        }
    }
}
