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

    /**
     * Peer UID for the conversation currently treated as open (e.g. for suppressing duplicate notifications).
     *
     * @return Active peer UID, or `null` when none.
     * @author udit
     */
    fun getOpenChatUserId(): String?
}
