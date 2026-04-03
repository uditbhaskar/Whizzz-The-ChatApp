package com.example.whizzz.data.prefs

import android.content.Context
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.repository.OpenChatTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * Tracks which chat thread is foregrounded so FCM can suppress duplicate banners.
 *
 * @author udit
 */
@Singleton
class OpenChatStore @Inject constructor(
    @ApplicationContext context: Context,
) : OpenChatTracker {
    private val sp = context.getSharedPreferences(WhizzzStrings.Prefs.FILE_NAME, Context.MODE_PRIVATE)

    /**
     * @param peerId Peer UID, or `null` when no chat is open.
     *
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
     * @author udit
     */
    fun getOpenChatUserId(): String? {
        val v = sp.getString(WhizzzStrings.Prefs.KEY_CURRENT_USER, WhizzzStrings.Prefs.VALUE_NO_ACTIVE_CHAT)
            ?: WhizzzStrings.Prefs.VALUE_NO_ACTIVE_CHAT
        return if (v == WhizzzStrings.Prefs.VALUE_NO_ACTIVE_CHAT) null else v
    }
}
