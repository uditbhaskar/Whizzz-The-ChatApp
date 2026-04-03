package com.example.whizzz.domain.text

/**
 * Caps for user-visible name and bio (profile + registration).
 * @author udit
 */
object DisplayTextLimits {
    const val MAX_BIO_LINES = 6
    const val MAX_USERNAME_CHARS = 25
    const val MAX_BIO_CHARS = 139
}

/**
 * Truncates to at most [maxLines] newline-separated segments.
 *
 * @receiver Source text, split on newlines.
 * @param maxLines Maximum number of lines to keep from the start.
 * @return This string truncated by line count when needed.
 * @author udit
 */
fun String.clampToMaxLines(maxLines: Int): String {
    if (maxLines <= 0) return ""
    val segments = split('\n')
    if (segments.size <= maxLines) return this
    return segments.take(maxLines).joinToString("\n")
}

/**
 * Single-line display name: first line only, trimmed, capped at [maxChars].
 *
 * @receiver Raw username text that may contain multiple lines.
 * @param maxChars Maximum character length for the username segment.
 * @return First trimmed line, truncated to [maxChars].
 * @author udit
 */
fun String.clampUsername(maxChars: Int = DisplayTextLimits.MAX_USERNAME_CHARS): String {
    val firstLine = lineSequence().firstOrNull()?.trim() ?: ""
    return firstLine.take(maxChars)
}

/**
 * Bio: at most [DisplayTextLimits.MAX_BIO_CHARS] characters, then at most [DisplayTextLimits.MAX_BIO_LINES] lines (newlines).
 *
 * @receiver Draft or stored bio text.
 * @return Text safe to show and persist.
 * @author udit
 */
fun String.clampBio(): String =
    take(DisplayTextLimits.MAX_BIO_CHARS).clampToMaxLines(DisplayTextLimits.MAX_BIO_LINES)
