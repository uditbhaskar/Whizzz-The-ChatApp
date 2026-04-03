/**
 * Length limits and string clamping for profile and registration fields (username, bio).
 *
 * @author udit
 */
package com.example.whizzz.domain.text

/**
 * Caps for user-visible name and bio (profile + registration).
 */
object DisplayTextLimits {
    const val MAX_BIO_LINES = 6
    const val MAX_USERNAME_CHARS = 32
}

/**
 * Truncates to at most [maxLines] newline-separated segments.
 */
fun String.clampToMaxLines(maxLines: Int): String {
    if (maxLines <= 0) return ""
    val segments = split('\n')
    if (segments.size <= maxLines) return this
    return segments.take(maxLines).joinToString("\n")
}

/**
 * Single-line display name: first line only, trimmed, capped at [maxChars].
 */
fun String.clampUsername(maxChars: Int = DisplayTextLimits.MAX_USERNAME_CHARS): String {
    val firstLine = lineSequence().firstOrNull()?.trim() ?: ""
    return firstLine.take(maxChars)
}
