package com.example.whizzz.core.common.errors

/**
 * Wraps a failure whose [message] is already a curated app string (for example from [com.example.whizzz.core.strings.WhizzzStrings.Errors]),
 * safe to show in the UI. [userFacingMessage] returns that message.
 *
 * @param message Copy safe to display (no raw Firebase / stack text).
 * @see Throwable.userFacingMessage
 * @author udit
 */
class UiSafeMessageException(
    message: String,
) : Exception(message)
