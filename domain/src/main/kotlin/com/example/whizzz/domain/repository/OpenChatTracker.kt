package com.example.whizzz.domain.repository

/**
 * Marks which peer conversation is active for notification policy.
 * @author udit
 */
interface OpenChatTracker {
    /**
     * Sets the peer UID for the open conversation, or clears it when leaving.
     *
     * @param peerId Other user's UID, or `null` when no chat is foregrounded.
     * @author udit
     */
    fun setActivePeer(peerId: String?)
}
