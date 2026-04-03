package com.example.whizzz.domain.model

import com.example.whizzz.core.strings.WhizzzStrings

/**
 * Domain representation of a chat user profile stored under the Realtime Database `Users` node.
 *
 * @property id Firebase Auth UID / user key.
 * @property username Display name shown in the app.
 * @property emailId Account email (maybe empty in stored data).
 * @property timestamp Registration or last-profile-update marker from the backend schema.
 * @property imageUrl HTTPS URL, `data:image/...;base64,...` (RTDB avatar), or `"default"`.
 * @property bio Short user bio.
 * @property status Presence string: treated as online only when it equals [WhizzzStrings.Defaults.PRESENCE_ONLINE] (case-insensitive) after trim.
 * @property searchKey Lowercase key used for prefix search in the database.
 * @author udit
 */
data class User(
    val id: String,
    val username: String,
    val emailId: String,
    val timestamp: String,
    val imageUrl: String,
    val bio: String,
    val status: String,
    val searchKey: String,
) {
    /**
     * Whether the user should be shown as online in the UI.
     * True only when [status] exactly matches the presence token (after trim), not substring match
     * (e.g. `"not online"` must not count as online).
     * @author udit
     */
    val isOnline: Boolean
        get() = status.trim().equals(WhizzzStrings.Defaults.PRESENCE_ONLINE, ignoreCase = true)
}
