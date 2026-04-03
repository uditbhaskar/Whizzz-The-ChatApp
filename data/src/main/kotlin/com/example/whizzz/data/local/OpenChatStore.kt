package com.example.whizzz.data.local

import android.content.Context
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.repository.OpenChatTracker
import androidx.core.content.edit

/**
 * Koin-provided [OpenChatTracker]: stores the foreground chat peer id in app SharedPreferences.
 *
 *
 * @param context Application context from [org.koin.android.ext.koin.androidContext].
 * @author udit
 */
class OpenChatStore(
    context: Context,
) : OpenChatTracker {
    private val sp = context.getSharedPreferences(WhizzzStrings.Prefs.FILE_NAME, Context.MODE_PRIVATE)

    /**
     * Persists the active peer id (or sentinel when cleared) for notification routing.
     *
     *
     * @param peerId Peer UID, or `null` when no chat is open.
     * @author udit
     */
    override fun setActivePeer(peerId: String?) {
        sp.edit {
            putString(
                WhizzzStrings.Prefs.KEY_CURRENT_USER,
                peerId ?: WhizzzStrings.Prefs.VALUE_NO_ACTIVE_CHAT,
            )
        }
    }

    /**
     * Reads the stored foreground peer id, if any.
     *
     *
     * @return Active peer UID, or `null` when none / sentinel value.
     * @author udit
     */
    fun getOpenChatUserId(): String? {
        val v = sp.getString(WhizzzStrings.Prefs.KEY_CURRENT_USER, WhizzzStrings.Prefs.VALUE_NO_ACTIVE_CHAT)
            ?: WhizzzStrings.Prefs.VALUE_NO_ACTIVE_CHAT
        return if (v == WhizzzStrings.Prefs.VALUE_NO_ACTIVE_CHAT) null else v
    }
}
