package com.example.whizzz.feature.profile.crop

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Creates a unique JPEG file under the app cache `share/` path (see `file_paths.xml`) and returns a
 * [FileProvider] content [Uri] for [androidx.activity.result.contract.ActivityResultContracts.TakePicture].
 *
 *
 * @param context Used for cache dir and package-qualified FileProvider authority.
 * @return A grantable content URI the camera app can write to.
 * @author udit
 */
internal fun createProfileCameraImageUri(context: Context): Uri {
    val dir = File(context.cacheDir, "share").apply { mkdirs() }
    val file = File(dir, "profile_capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
}
