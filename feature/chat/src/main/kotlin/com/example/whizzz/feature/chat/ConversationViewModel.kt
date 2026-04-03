package com.example.whizzz.feature.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.coroutines.stateInWhileSubscribed
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import com.example.whizzz.domain.model.ChatMessage
import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.repository.AuthRepository
import com.example.whizzz.domain.repository.ChatRepository
import com.example.whizzz.domain.repository.OpenChatTracker
import com.example.whizzz.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 1:1 thread: messages, compose field, send. Push is handled by Cloud Functions on new `Chats` rows.
 *
 * @author udit
 */
@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val openChatTracker: OpenChatTracker,
    private val networkConnectivity: NetworkConnectivity,
) : ViewModel() {

    val peerId: String = checkNotNull(savedStateHandle.get<String>(WhizzzStrings.Nav.ARG_PEER_ID))

    private val myIdFlow = authRepository.authState().map { it?.uid }

    private val _streamRetry = MutableStateFlow(0)
    private val _streamError = MutableStateFlow<String?>(null)
    val streamError: StateFlow<String?> = _streamError.asStateFlow()

    private val _sendMessageError = MutableStateFlow<String?>(null)
    val sendMessageError: StateFlow<String?> = _sendMessageError.asStateFlow()

    val myUserId: StateFlow<String?> = myIdFlow.stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = null,
    )

    private val uidWithRetry = combine(myIdFlow, _streamRetry) { uid, _ -> uid }

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: StateFlow<List<ChatMessage>> = uidWithRetry
        .flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                chatRepository.observeConversation(uid, peerId)
                    .catch { e ->
                        _streamError.value = e.userFacingMessage(
                            offlineFallback = WhizzzStrings.Errors.LOAD_MESSAGES_FAILED,
                            genericFallback = WhizzzStrings.Errors.LOAD_MESSAGES_FAILED,
                        )
                        emit(emptyList())
                    }
            }
        }
        .stateInWhileSubscribed(
            scope = viewModelScope,
            initialValue = emptyList(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val peerUser: StateFlow<User?> = _streamRetry
        .flatMapLatest {
            userRepository.observeUser(peerId)
                .catch { e ->
                    _streamError.update { existing ->
                        existing ?: e.userFacingMessage(
                            offlineFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                            genericFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                        )
                    }
                    emit(null)
                }
        }
        .stateInWhileSubscribed(
            scope = viewModelScope,
            initialValue = null,
        )

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()

    init {
        openChatTracker.setActivePeer(peerId)
    }

    override fun onCleared() {
        openChatTracker.setActivePeer(null)
        super.onCleared()
    }

    fun dismissStreamError() {
        _streamError.value = null
    }

    /**
     * Re-subscribes to Firebase after a listener failure (e.g. offline).
     */
    fun retryStreams() {
        _streamError.value = null
        _streamRetry.update { it + 1 }
    }

    fun clearSendMessageError() {
        _sendMessageError.value = null
    }

    /**
     * @author udit
     */
    fun setPresenceOnline() {
        viewModelScope.launch {
            userRepository.setPresence(WhizzzStrings.Defaults.PRESENCE_ONLINE)
        }
    }

    /**
     * @author udit
     */
    fun setPresenceOffline() {
        viewModelScope.launch {
            userRepository.setPresence(WhizzzStrings.Defaults.STATUS_OFFLINE)
        }
    }

    /**
     * @author udit
     */
    fun onDraftChange(value: String) {
        _draft.update { value }
    }

    /**
     * @author udit
     */
    fun markPeerMessagesSeen() {
        viewModelScope.launch {
            val uid = myUserId.value ?: return@launch
            chatRepository.markPeerMessagesSeen(peerId, uid)
        }
    }

    /**
     * @author udit
     */
    fun send() {
        viewModelScope.launch {
            val uid = myUserId.value ?: return@launch
            val text = _draft.value.trim()
            if (text.isEmpty()) return@launch
            if (!networkConnectivity.isOnline.value) {
                _sendMessageError.value = WhizzzStrings.Errors.MESSAGE_SEND_FAILED
                return@launch
            }
            val ts = System.currentTimeMillis().toString()
            chatRepository.sendMessage(peerId, uid, text, ts)
                .onSuccess {
                    _draft.value = ""
                    _sendMessageError.value = null
                }
                .onFailure { e ->
                    _sendMessageError.value = e.userFacingMessage(
                        offlineFallback = WhizzzStrings.Errors.MESSAGE_SEND_FAILED,
                        genericFallback = WhizzzStrings.Errors.MESSAGE_SEND_FAILED,
                    )
                }
        }
    }
}
