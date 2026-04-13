package com.example.whizzz.feature.profile.crop

import android.annotation.SuppressLint
import android.content.Context

/**
 * Resolves ids for resources merged from `android-image-cropper` using the host app package.
 * Needed when the module `R` does not expose transitive library symbols (e.g. non-transitive R classes).
 * @author udit
 */
@SuppressLint("DiscouragedApi")
internal fun Context.mergedLibraryResId(name: String, type: String): Int =
    resources.getIdentifier(name, type, packageName)
