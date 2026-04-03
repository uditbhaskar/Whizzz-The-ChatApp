package com.example.whizzz.domain.repository

/**
 * Remembers which 1:1 thread is in the foreground (for notification suppression).
 *
 * @author udit
 */
interface OpenChatTracker {
    /**
     * @param peerId Other user's UID, or `null` when leaving the thread.
     *
     * @author udit
     */
    fun setActivePeer(peerId: String?)
}
