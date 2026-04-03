/**
 * JPEG resize/re-encode pipeline so profile uploads stay under a byte budget for Realtime Database `data:` URIs.
 *
 * @author udit
 */
package com.example.whizzz.feature.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import kotlin.math.max
import androidx.core.graphics.scale

/**
 * Decodes [original], scales down as needed, and re-encodes as JPEG until size is at most [TARGET_MAX_JPEG_BYTES].
 */
internal fun jpegBytesForProfileUpload(original: ByteArray): ByteArray {
    var bmp: Bitmap = decodeForProcessing(original) ?: return original
    try {
        var maxSide = 1600
        var quality = 90
        repeat(28) {
            val scaled = scaleToMaxSide(bmp, maxSide)
            if (scaled !== bmp) {
                bmp.recycle()
                bmp = scaled
            }
            val jpeg = compressJpeg(bmp, quality)
            if (jpeg.size <= TARGET_MAX_JPEG_BYTES) return jpeg
            if (quality > 48) {
                quality -= 8
            } else {
                maxSide = (maxSide * 3 / 4).coerceAtLeast(180)
                quality = 85
            }
        }
        val tiny = scaleToMaxSide(bmp, 160)
        if (tiny !== bmp) {
            bmp.recycle()
            bmp = tiny
        }
        return compressJpeg(bmp, 38)
    } finally {
        if (!bmp.isRecycled) bmp.recycle()
    }
}

private const val TARGET_MAX_JPEG_BYTES = 380_000

/** Decode full image, or subsample if bounds are very large (memory-safe). */
private fun decodeForProcessing(original: ByteArray): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(original, 0, original.size, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
        return BitmapFactory.decodeByteArray(original, 0, original.size)
    }
    var sample = 1
    val w = bounds.outWidth
    val h = bounds.outHeight
    while (w / sample > 4096 || h / sample > 4096) sample *= 2
    val opts = BitmapFactory.Options().apply { inSampleSize = sample }
    return BitmapFactory.decodeByteArray(original, 0, original.size, opts)
}

private fun scaleToMaxSide(bitmap: Bitmap, maxSide: Int): Bitmap {
    val w = bitmap.width
    val h = bitmap.height
    val longest = max(w, h)
    if (longest <= maxSide) return bitmap
    val scale = maxSide.toFloat() / longest
    return bitmap.scale((w * scale).toInt().coerceAtLeast(1), (h * scale).toInt().coerceAtLeast(1))
}

private fun compressJpeg(bitmap: Bitmap, quality: Int): ByteArray {
    val q = quality.coerceIn(35, 95)
    ByteArrayOutputStream().use { os ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, q, os)
        return os.toByteArray()
    }
}
