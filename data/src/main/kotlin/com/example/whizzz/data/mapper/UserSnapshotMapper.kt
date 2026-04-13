package com.example.whizzz.data.mapper

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.model.User
import com.google.firebase.database.DataSnapshot

/**
 * Builds a [User] when `id` is present; fills missing optional strings with defaults.
 *
 * @receiver User node under `Users`.
 * @return [User] or `null` if `id` is missing.
 * @author udit
 */
internal fun DataSnapshot.toUserOrNull(): User? {
    val id = child(WhizzzStrings.Db.CHILD_ID).getValue(String::class.java) ?: return null
    return User(
        id = id,
        username = child(WhizzzStrings.Db.CHILD_USERNAME).getValue(String::class.java).orEmpty(),
        emailId = child(WhizzzStrings.Db.CHILD_EMAIL_ID).getValue(String::class.java).orEmpty(),
        timestamp = child(WhizzzStrings.Db.CHILD_TIMESTAMP).getValue(String::class.java).orEmpty(),
        imageUrl = child(WhizzzStrings.Db.CHILD_IMAGE_URL).getValue(String::class.java)
            .takeUnless { it.isNullOrBlank() }
            ?: WhizzzStrings.Defaults.PROFILE_IMAGE,
        bio = child(WhizzzStrings.Db.CHILD_BIO).getValue(String::class.java).orEmpty(),
        status = child(WhizzzStrings.Db.CHILD_STATUS).getValue(String::class.java).orEmpty(),
        searchKey = child(WhizzzStrings.Db.CHILD_SEARCH).getValue(String::class.java).orEmpty(),
    )
}
