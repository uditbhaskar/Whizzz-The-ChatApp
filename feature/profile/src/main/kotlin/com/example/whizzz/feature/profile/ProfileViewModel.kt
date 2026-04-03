package com.example.whizzz.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import com.example.whizzz.domain.repository.UserRepository
import com.example.whizzz.domain.text.DisplayTextLimits
import com.example.whizzz.domain.text.clampToMaxLines
import com.example.whizzz.domain.text.clampUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Profile tab: observe self, edit username/bio, upload avatar.
 *
 * @author udit
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val networkConnectivity: NetworkConnectivity,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        startObserveUser()
    }

    private fun startObserveUser() {
        observeJob?.cancel()
        observeJob = userRepository.observeCurrentUser()
            .onEach { user ->
                _state.update {
                    it.copy(user = user, profileLoadError = null)
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        user = null,
                        profileLoadError = e.userFacingMessage(
                            offlineFallback = WhizzzStrings.Errors.LOAD_PROFILE_FAILED,
                            genericFallback = WhizzzStrings.Errors.LOAD_PROFILE_FAILED,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.RetryLoadProfile -> {
                _state.update { it.copy(profileLoadError = null) }
                startObserveUser()
            }
            ProfileUiEvent.OpenUsernameDialog -> _state.update {
                it.copy(
                    dialog = ProfileDialog.Username,
                    dialogText = it.user?.username.orEmpty().clampUsername(),
                    error = null,
                )
            }
            ProfileUiEvent.OpenBioDialog -> _state.update {
                it.copy(
                    dialog = ProfileDialog.Bio,
                    dialogText = it.user?.bio.orEmpty().clampToMaxLines(DisplayTextLimits.MAX_BIO_LINES),
                    error = null,
                )
            }
            ProfileUiEvent.DismissDialog -> _state.update {
                it.copy(dialog = ProfileDialog.None, dialogText = "", error = null)
            }
            is ProfileUiEvent.DialogTextChanged -> _state.update {
                val next = when (it.dialog) {
                    ProfileDialog.Bio -> event.value.clampToMaxLines(DisplayTextLimits.MAX_BIO_LINES)
                    ProfileDialog.Username -> event.value.clampUsername()
                    else -> event.value
                }
                it.copy(dialogText = next)
            }
            ProfileUiEvent.SaveDialog -> saveDialog()
            is ProfileUiEvent.PhotoPicked -> uploadPhoto(event.jpegBytes)
        }
    }

    private fun saveDialog() {
        val dialog = _state.value.dialog
        val raw = _state.value.dialogText.trim()
        val text = when (dialog) {
            ProfileDialog.Bio -> raw.clampToMaxLines(DisplayTextLimits.MAX_BIO_LINES)
            ProfileDialog.Username -> raw.clampUsername()
            else -> raw
        }
        if (!networkConnectivity.isOnline.value) {
            _state.update {
                it.copy(error = WhizzzStrings.Errors.PROFILE_SAVE_OFFLINE)
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            when (dialog) {
                ProfileDialog.Username -> userRepository.updateUsername(text)
                ProfileDialog.Bio -> userRepository.updateBio(text)
                ProfileDialog.None -> Result.success(Unit)
            }
                .onSuccess {
                    _state.update { it.copy(loading = false, dialog = ProfileDialog.None, dialogText = "") }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.PROFILE_SAVE_OFFLINE,
                                genericFallback = WhizzzStrings.Errors.UPDATE_FAILED,
                            ),
                        )
                    }
                }
        }
    }

    private fun uploadPhoto(bytes: ByteArray) {
        if (!networkConnectivity.isOnline.value) {
            _state.update { it.copy(error = WhizzzStrings.Errors.PROFILE_PHOTO_OFFLINE) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(photoUploading = true, error = null) }
            userRepository.uploadProfileImage(bytes, WhizzzStrings.Media.JPEG_EXTENSION).fold(
                onSuccess = { _state.update { it.copy(photoUploading = false, error = null) } },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            photoUploading = false,
                            error = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.PROFILE_PHOTO_OFFLINE,
                                genericFallback = WhizzzStrings.Errors.UPLOAD_FAILED,
                            ),
                        )
                    }
                },
            )
        }
    }
}
