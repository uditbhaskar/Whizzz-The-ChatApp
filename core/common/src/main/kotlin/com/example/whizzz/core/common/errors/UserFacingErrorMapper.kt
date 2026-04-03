package com.example.whizzz.core.common.errors

import android.util.Log
import com.example.whizzz.core.strings.WhizzzStrings
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private object UserFacingErrorMapperTokens {
    const val LOG_TAG = "WhizzzAppError"
    const val LOG_FAILURE_SUMMARY = "Failure (user-facing mapping; full cause below)"
    const val MESSAGE_JOIN_SEPARATOR = " "

    object OfflineMessageSubstring {
        const val NETWORK_UNREACHABLE = "network is unreachable"
        const val CONNECTION_RESET = "connection reset"
        const val BROKEN_PIPE = "broken pipe"
        const val FAILED_TO_CONNECT = "failed to connect"
        const val UNABLE_TO_RESOLVE_HOST = "unable to resolve host"
        const val NETWORK_ERROR_UNDERSCORE = "network_error"
        const val NETWORK_ERROR_SPACED = "network error"
        const val UNAVAILABLE = "unavailable"
        const val NETWORK = "network"
    }

    object AggregatedBlobSubstring {
        const val NETWORK_ERROR_UNDERSCORE = "network_error"
        const val NETWORK_REQUEST_FAILED = "network request failed"
        const val DISCONNECTED = "disconnected"
        const val FIREBASE = "firebase"
        const val PERMISSION_DENIED_SPACED = "permission denied"
        const val PERMISSION_DENIED_UNDERSCORE = "permission_denied"
        const val UNAUTHENTICATED = "unauthenticated"
        const val INDEX_NOT_DEFINED = "index not defined"
        const val INDEX_ON = "indexon"
    }
}

private fun Throwable.rootCause(): Throwable {
    var c = this
    while (c.cause != null && c.cause != c) {
        c = c.cause!!
    }
    return c
}

private fun Throwable.isLikelyOfflineCause(): Boolean {
    val o = UserFacingErrorMapperTokens.OfflineMessageSubstring
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
            if (m.contains(o.NETWORK_UNREACHABLE)) return true
            if (m.contains(o.CONNECTION_RESET)) return true
            if (m.contains(o.BROKEN_PIPE)) return true
            if (m.contains(o.FAILED_TO_CONNECT)) return true
            if (m.contains(o.UNABLE_TO_RESOLVE_HOST)) return true
        }
        val msg = t.message?.lowercase().orEmpty()
        if (msg.contains(o.NETWORK_ERROR_UNDERSCORE) || msg.contains(o.NETWORK_ERROR_SPACED)) return true
        if (msg.contains(o.UNAVAILABLE) && msg.contains(o.NETWORK)) return true
        t = t.cause
    }
    return false
}

private fun Throwable.collectMessages(): String {
    val sep = UserFacingErrorMapperTokens.MESSAGE_JOIN_SEPARATOR
    val parts = mutableListOf<String>()
    var t: Throwable? = this
    val seen = mutableSetOf<Throwable>()
    while (t != null && t !in seen) {
        seen.add(t)
        t.message?.trim()?.takeIf { it.isNotEmpty() }?.let { parts.add(it) }
        t = t.cause
    }
    return parts.joinToString(sep).lowercase()
}

/**
 * Maps throwable to a single UI-safe line. Logs the full cause under [UserFacingErrorMapperTokens.LOG_TAG].
 * @author udit
 */
fun Throwable.userFacingMessage(
    offlineFallback: String = WhizzzStrings.Errors.OFFLINE_GENERIC,
    genericFallback: String = WhizzzStrings.Errors.GENERIC,
): String {
    val tok = UserFacingErrorMapperTokens
    val blobTok = UserFacingErrorMapperTokens.AggregatedBlobSubstring
    Log.w(tok.LOG_TAG, tok.LOG_FAILURE_SUMMARY, this)
    if (rootCause().isLikelyOfflineCause() || isLikelyOfflineCause()) {
        return offlineFallback
    }
    val blob = collectMessages()
    if (blob.contains(blobTok.NETWORK_ERROR_UNDERSCORE) || blob.contains(blobTok.NETWORK_REQUEST_FAILED)) {
        return offlineFallback
    }
    if (blob.contains(blobTok.DISCONNECTED) && blob.contains(blobTok.FIREBASE)) {
        return offlineFallback
    }
    val safe = when {
        this is UiSafeMessageException -> this
        cause is UiSafeMessageException -> cause as UiSafeMessageException
        else -> null
    }
    if (safe != null) {
        return safe.message?.trim()?.takeIf { it.isNotEmpty() } ?: genericFallback
    }
    if (blob.contains(blobTok.PERMISSION_DENIED_SPACED) || blob.contains(blobTok.PERMISSION_DENIED_UNDERSCORE)) {
        return WhizzzStrings.Errors.DATA_ACCESS_DENIED
    }
    if (blob.contains(blobTok.UNAUTHENTICATED)) {
        return WhizzzStrings.Errors.DATA_ACCESS_DENIED
    }
    if (blob.contains(blobTok.INDEX_NOT_DEFINED) || blob.contains(blobTok.INDEX_ON)) {
        return WhizzzStrings.Errors.LIST_TEMPORARILY_UNAVAILABLE
    }
    return genericFallback
}
