/**
 * Throwable inspection helpers for offline detection and mapping to user-visible error strings.
 *
 * @author udit
 */
package com.example.whizzz.core.common.errors

import com.example.whizzz.core.strings.WhizzzStrings
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** Walks the cause chain to the innermost [Throwable]. */
private fun Throwable.rootCause(): Throwable {
    var c = this
    while (c.cause != null && c.cause != c) {
        c = c.cause!!
    }
    return c
}

/**
 * Returns true if this exception or any suggests loss of network connectivity.
 */
private fun Throwable.isLikelyOfflineCause(): Boolean {
    var t: Throwable? = this
    val seen = mutableSetOf<Throwable>()
    while (t != null && t !in seen) {
        seen.add(t)
        when (t) {
            is UnknownHostException -> return true
            is SocketTimeoutException -> return true
            is ConnectException -> return true
            is java.net.NoRouteToHostException -> return true
        }
        if (t is IOException) {
            val m = t.message?.lowercase().orEmpty()
            if (m.contains("network is unreachable")) return true
            if (m.contains("connection reset")) return true
            if (m.contains("broken pipe")) return true
            if (m.contains("failed to connect")) return true
            if (m.contains("unable to resolve host")) return true
        }
        val msg = t.message?.lowercase().orEmpty()
        if (msg.contains("network_error") || msg.contains("network error")) return true
        if (msg.contains("unavailable") && msg.contains("network")) return true
        t = t.cause
    }
    return false
}

/** Lowercase concatenation of non-blank [Throwable.message] values along the cause chain. */
private fun Throwable.collectMessages(): String {
    val parts = mutableListOf<String>()
    var t: Throwable? = this
    val seen = mutableSetOf<Throwable>()
    while (t != null && t !in seen) {
        seen.add(t)
        t.message?.trim()?.takeIf { it.isNotEmpty() }?.let { parts.add(it) }
        t = t.cause
    }
    return parts.joinToString(" ").lowercase()
}

/**
 * Produces a single line safe to show in UI: prefers [offlineFallback] when the cause looks offline.
 *
 * @param offlineFallback Shown when connectivity likely failed.
 * @param genericFallback Shown when there is no non-technical message to expose.
 */
fun Throwable.userFacingMessage(
    offlineFallback: String = WhizzzStrings.Errors.OFFLINE_GENERIC,
    genericFallback: String = WhizzzStrings.Errors.GENERIC,
): String {
    if (rootCause().isLikelyOfflineCause() || isLikelyOfflineCause()) {
        return offlineFallback
    }
    val blob = collectMessages()
    if (blob.contains("network_error") || blob.contains("network request failed")) {
        return offlineFallback
    }
    if (blob.contains("disconnected") && blob.contains("firebase")) {
        return offlineFallback
    }
    message?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
    cause?.message?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
    return genericFallback
}
