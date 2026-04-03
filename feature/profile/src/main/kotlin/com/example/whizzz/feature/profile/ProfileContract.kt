package com.example.whizzz.feature.profile

import com.example.whizzz.domain.model.User

/**
 * MVI contracts for the profile tab.
 *
 * @author udit
 */
data class ProfileUiState(
    val user: User? = null,
    /** Fails when the profile stream errors (e.g. offline). */
    val profileLoadError: String? = null,
    val loading: Boolean = false,
    val dialog: ProfileDialog = ProfileDialog.None,
    val dialogText: String = "",
    val error: String? = null,
    val photoUploading: Boolean = false,
)

/**
 * @author udit
 */
sealed interface ProfileDialog {
    data object None : ProfileDialog
    data object Username : ProfileDialog
    data object Bio : ProfileDialog
}

/**
 * @author udit
 */
sealed interface ProfileUiEvent {
    data object RetryLoadProfile : ProfileUiEvent
    data object OpenUsernameDialog : ProfileUiEvent
    data object OpenBioDialog : ProfileUiEvent
    data object DismissDialog : ProfileUiEvent
    data class DialogTextChanged(val value: String) : ProfileUiEvent
    data object SaveDialog : ProfileUiEvent
    data class PhotoPicked(val jpegBytes: ByteArray) : ProfileUiEvent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PhotoPicked

            return jpegBytes.contentEquals(other.jpegBytes)
        }

        override fun hashCode(): Int {
            return jpegBytes.contentHashCode()
        }
    }
}
