package com.example.whizzz.core.ui.media

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.whizzz.core.strings.WhizzzStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * Loads a profile [imageUrl] (default / data URI / https), re-encodes as JPEG, writes a cache file for sharing.
 *
 * @receiver Android [Context] used for cache paths.
 * @param imageUrl Avatar source string.
 * @return Temporary JPEG [File] in cache, or `null` when decoding fails.
 * @author udit
 */
suspend fun Context.profileImageToShareJpegFile(imageUrl: String): File? = withContext(Dispatchers.IO) {
    val jpeg = profileImageToJpegBytes(imageUrl) ?: return@withContext null
    val dir = File(cacheDir, "share").apply { mkdirs() }
    val out = File(dir, "whizzz_share_${System.currentTimeMillis()}.jpg")
    FileOutputStream(out).use { it.write(jpeg) }
    out
}

/**
 * Starts a chooser that shares [file] as `image/jpeg` via [FileProvider].
 *
 * @receiver Host [Context] for URI authority and activity start.
 * @param file JPEG produced by [profileImageToShareJpegFile].
 * @author udit
 */
fun Context.shareProfileImageFile(file: File) {
    val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, WhizzzStrings.Ui.SHARE))
}

/**
 * Saves JPEG bytes to `Pictures/Whizzz` via MediaStore.
 *
 * @receiver [Context] used to access the content resolver.
 * @param bytes JPEG file contents.
 * @return `true` when insert and stream write succeed.
 * @author udit
 */
suspend fun Context.saveJpegToGallery(bytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
    try {
        val resolver = contentResolver
        val name = "Whizzz_${System.currentTimeMillis()}.jpg"
        val values = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/Whizzz",
                )
            }
        }
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(collection, values) ?: return@withContext false
        resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: return@withContext false
        true
    } catch (_: Exception) {
        false
    }
}

/**
 * Whether the URL can be exported (not the built-in default placeholder).
 *
 * @param imageUrl Raw profile image reference.
 * @return `true` for non-blank http(s) or `data:image/` URIs excluding the default token.
 * @author udit
 */
fun isExportableProfileImageUrl(imageUrl: String): Boolean {
    if (imageUrl.isBlank() || imageUrl == WhizzzStrings.Defaults.PROFILE_IMAGE) return false
    return imageUrl.startsWith("data:image/", ignoreCase = true) ||
        imageUrl.startsWith("http://", ignoreCase = true) ||
        imageUrl.startsWith("https://", ignoreCase = true)
}

/**
 * Decodes [imageUrl] to bitmap bytes and recompresses as JPEG quality 92.
 *
 * @param imageUrl Exportable profile source.
 * @return JPEG bytes or `null` when decode/compress fails.
 * @author udit
 */
private fun profileImageToJpegBytes(imageUrl: String): ByteArray? {
    if (!isExportableProfileImageUrl(imageUrl)) return null
    val raw = when {
        imageUrl.startsWith("data:image/", ignoreCase = true) -> decodeDataUriToBytes(imageUrl) ?: return null
        else -> URL(imageUrl).openStream().use { it.readBytes() }
    }
    val bmp = BitmapFactory.decodeByteArray(raw, 0, raw.size) ?: return null
    val bass = ByteArrayOutputStream()
    if (!bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, bass)) return null
    return bass.toByteArray()
}

/**
 * Extracts base64 payload from a `data:image/...;base64,...` URI.
 *
 * @param dataUri Full data URI string.
 * @return Raw decoded bytes or `null` if malformed.
 * @author udit
 */
private fun decodeDataUriToBytes(dataUri: String): ByteArray? {
    if (!dataUri.startsWith("data:image/", ignoreCase = true)) return null
    val comma = dataUri.indexOf(',')
    if (comma < 0) return null
    val header = dataUri.substring(0, comma)
    if (!header.contains("base64", ignoreCase = true)) return null
    val b64 = dataUri.substring(comma + 1).trim()
    return try {
        Base64.decode(b64, Base64.DEFAULT)
    } catch (_: Throwable) {
        null
    }
}
