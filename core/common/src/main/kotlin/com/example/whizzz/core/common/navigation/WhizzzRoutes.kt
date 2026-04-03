/**
 * Central route strings for androidx.navigation.compose.NavHost, aligned with [WhizzzStrings.Nav].
 *
 * @author udit
 */
package com.example.whizzz.core.common.navigation

import com.example.whizzz.core.strings.WhizzzStrings

/** Static destinations and helpers for building chat routes. */
object WhizzzRoutes {
    const val SPLASH = WhizzzStrings.Nav.SPLASH
    const val LOGIN = WhizzzStrings.Nav.LOGIN
    const val REGISTER = WhizzzStrings.Nav.REGISTER
    const val FORGOT = WhizzzStrings.Nav.FORGOT
    const val HOME = WhizzzStrings.Nav.HOME
    const val CHAT_PATTERN = WhizzzStrings.Nav.CHAT_PATTERN

    /**
     * @param peerId Firebase UID of the other user (path-safe).
     *
     * @author udit
     */
    fun chat(peerId: String): String =
        "${WhizzzStrings.Nav.CHAT_PREFIX}/$peerId"
}
