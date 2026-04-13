package com.example.whizzz.feature.chat.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.coroutines.stateInWhileSubscribed
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.usecase.chat.MarkPeerMessagesSeenUseCase
import com.example.whizzz.domain.usecase.chat.ObserveConversationMessagesUseCase
import com.example.whizzz.domain.usecase.connectivity.ObserveNetworkOnlineUseCase
import com.example.whizzz.domain.usecase.user.ObserveUserProfileUseCase
import com.example.whizzz.domain.usecase.chat.SendConversationMessageUseCase
import com.example.whizzz.domain.usecase.chat.SetActiveChatPeerUseCase
import com.example.whizzz.domain.usecase.auth.StreamAuthSessionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI [androidx.lifecycle.ViewModel] for a single chat thread: [state] + [onEvent].
 *
 * Composition-only: delegates to domain use cases (Clean Architecture).
 * @author udit
 */
class ConversationViewModel(
    savedStateHandle: SavedStateHandle,
    streamAuthSession: StreamAuthSessionUseCase,
    private val observeConversationMessages: ObserveConversationMessagesUseCase,
    private val observeUserProfile: ObserveUserProfileUseCase,
    private val sendConversationMessage: SendConversationMessageUseCase,
    private val markPeerMessagesSeen: MarkPeerMessagesSeenUseCase,
    private val setActiveChatPeer: SetActiveChatPeerUseCase,
    observeNetworkOnline: ObserveNetworkOnlineUseCase,
) : ViewModel() {
    val peerId: String = checkNotNull(savedStateHandle.get<String>(WhizzzStrings.Nav.ARG_PEER_ID))

    private val myIdFlow = streamAuthSession().map { it?.uid }

    private val _streamRetry = MutableStateFlow(0)
    private val _streamError = MutableStateFlow<String?>(null)
    private val _sendError = MutableStateFlow<String?>(null)
    private val _draft = MutableStateFlow("")

    private val isOnlineFlow = observeNetworkOnline()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val uidWithRetry = combine(myIdFlow, _streamRetry) { uid, _ -> uid }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val messagesFlow = uidWithRetry.flatMapLatest { uid ->
        if (uid == null) {
            flowOf(emptyList())
        } else {
            observeConversationMessages(uid, peerId)
                .catch { e ->
                    _streamError.value = e.userFacingMessage(
                        offlineFallback = WhizzzStrings.Errors.LOAD_MESSAGES_FAILED,
                        genericFallback = WhizzzStrings.Errors.LOAD_MESSAGES_FAILED,
                    )
                    emit(emptyList())
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val peerFlow = _streamRetry.flatMapLatest {
        observeUserProfile(peerId)
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

    private val myUserIdState = myIdFlow.stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = null,
    )

    private val messagesState = messagesFlow.stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = emptyList(),
    )

    private val peerState = peerFlow.stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = null,
    )

    /**
     * Unified UI model for ConversationRoute.
     * @author udit
     */
    val state: StateFlow<ConversationUiState> = combine(
        combine(myUserIdState, messagesState, peerState) { myId, msgs, peer ->
            Triple(myId, msgs, peer)
        },
        _draft,
        _streamError,
        _sendError,
    ) { triple, draft, se, sendE ->
        val (myId, msgs, peer) = triple
        ConversationUiState(
            myUserId = myId,
            messages = msgs,
            peerUser = peer,
            draft = draft,
            streamError = se,
            sendError = sendE,
        )
    }.stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = ConversationUiState(),
    )

    init {
        setActiveChatPeer(peerId)
    }

    override fun onCleared() {
        setActiveChatPeer(null)
        super.onCleared()
    }

    /**
     *
     * @param event User or lifecycle intent.
     * @author udit
     */
    fun onEvent(event: ConversationUiEvent) {
        when (event) {
            is ConversationUiEvent.DraftChanged -> _draft.value = event.value
            ConversationUiEvent.Send -> send()
            ConversationUiEvent.DismissStreamError -> {
                _streamError.value = null
            }
            ConversationUiEvent.RetryStreams -> {
                _streamError.value = null
                _streamRetry.update { it + 1 }
            }
            ConversationUiEvent.ClearSendError -> {
                _sendError.value = null
            }
            ConversationUiEvent.MarkPeerSeen -> markPeerMessagesSeenInternal()
        }
    }

    private fun markPeerMessagesSeenInternal() {
        viewModelScope.launch {
            val uid = state.value.myUserId ?: return@launch
            markPeerMessagesSeen(peerId, uid)
        }
    }

    private fun send() {
        viewModelScope.launch {
            val uid = state.value.myUserId ?: return@launch
            val text = state.value.draft.trim()
            if (text.isEmpty()) return@launch
            if (!isOnlineFlow.value) {
                _sendError.value = WhizzzStrings.Errors.MESSAGE_SEND_FAILED
                return@launch
            }
            val ts = System.currentTimeMillis().toString()
            sendConversationMessage(peerId, uid, text, ts)
                .onSuccess {
                    _draft.value = ""
                    _sendError.value = null
                }
                .onFailure { e ->
                    _sendError.value = e.userFacingMessage(
                        offlineFallback = WhizzzStrings.Errors.MESSAGE_SEND_FAILED,
                        genericFallback = WhizzzStrings.Errors.MESSAGE_SEND_FAILED,
                    )
                }
        }
    }
}
