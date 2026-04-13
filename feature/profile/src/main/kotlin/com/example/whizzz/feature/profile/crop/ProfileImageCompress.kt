package com.example.whizzz.feature.profile.crop

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import kotlin.math.max
import androidx.core.graphics.scale

/**
 * Decodes [original], scales down as needed, and re-encodes as JPEG until size is at most the internal byte budget.
 *
 *
 * @param original Raw image bytes (any format [BitmapFactory] accepts).
 * @return JPEG bytes sized for Realtime Database `data:` URI limits, or [original] if decode fails.
 * @author udit
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

/**
 * Upper bound on JPEG payload size so `data:image/jpeg;base64,...` avatars stay practical in Realtime Database.
 * @author udit
 */
private const val TARGET_MAX_JPEG_BYTES = 380_000

/**
 * Decodes full image or subsamples when dimensions are very large (memory-safe).
 *
 *
 * @param original Raw bytes to decode.
 * @return Decoded bitmap or null on failure.
 * @author udit
 */
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

/**
 * Scales [bitmap] so its longest side is at most [maxSide], or returns the same instance if already small enough.
 *
 *
 * @param bitmap Source bitmap.
 * @param maxSide Maximum width or height.
 * @return Scaled or unchanged bitmap.
 * @author udit
 */
private fun scaleToMaxSide(bitmap: Bitmap, maxSide: Int): Bitmap {
    val w = bitmap.width
    val h = bitmap.height
    val longest = max(w, h)
    if (longest <= maxSide) return bitmap
    val scale = maxSide.toFloat() / longest
    return bitmap.scale((w * scale).toInt().coerceAtLeast(1), (h * scale).toInt().coerceAtLeast(1))
}

/**
 * Encodes [bitmap] to JPEG with quality clamped to a safe range.
 *
 *
 * @param bitmap Bitmap to compress.
 * @param quality Target JPEG quality (clamped).
 * @return JPEG byte array.
 * @author udit
 */
private fun compressJpeg(bitmap: Bitmap, quality: Int): ByteArray {
    val q = quality.coerceIn(35, 95)
    ByteArrayOutputStream().use { os ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, q, os)
        return os.toByteArray()
    }
}
