package com.example.whizzz.core.common.navigation

import com.example.whizzz.core.strings.WhizzzStrings

/**
 * Mirrors [WhizzzStrings.Nav] with helpers that build concrete deep-link style paths.
 * @author udit
 */
object WhizzzRoutes {
    const val SPLASH = WhizzzStrings.Nav.SPLASH
    const val LOGIN = WhizzzStrings.Nav.LOGIN
    const val REGISTER = WhizzzStrings.Nav.REGISTER
    const val FORGOT = WhizzzStrings.Nav.FORGOT
    const val HOME = WhizzzStrings.Nav.HOME
    const val CHAT_PATTERN = WhizzzStrings.Nav.CHAT_PATTERN
    const val PEER_PROFILE_PATTERN = WhizzzStrings.Nav.PEER_PROFILE_PATTERN

    /**
     * Builds the `chat/{peerId}` destination string for NavHost.
     *
     * @param peerId Firebase UID of the other user (path-safe).
     * @return Route string including the chat prefix and [peerId].
     * @author udit
     */
    fun chat(peerId: String): String =
        "${WhizzzStrings.Nav.CHAT_PREFIX}/$peerId"

    /**
     * Builds the peer profile route for a given Firebase UID.
     *
     * @param userId Firebase UID of the profile to show (path-safe).
     * @return Route string including the peer-profile prefix and [userId].
     * @author udit
     */
    fun peerProfile(userId: String): String =
        "${WhizzzStrings.Nav.PEER_PROFILE_PREFIX}/$userId"
}
