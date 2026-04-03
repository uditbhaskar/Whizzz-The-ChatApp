package com.example.whizzz.feature.home.presentation.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.model.ChatListEntry
import com.example.whizzz.domain.usecase.auth.StreamAuthSessionUseCase
import com.example.whizzz.domain.usecase.chat.FetchChatListPageUseCase
import com.example.whizzz.domain.usecase.chat.ObserveChatPartnerIdsUseCase
import com.example.whizzz.domain.usecase.user.FetchUsersByIdsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI [androidx.lifecycle.ViewModel] for the Chats tab.
 *
 * Uses [StreamAuthSessionUseCase], [ObserveChatPartnerIdsUseCase], [FetchChatListPageUseCase], and [FetchUsersByIdsUseCase].
 * @author udit
 */
@OptIn(FlowPreview::class)
class ConversationsViewModel(
    streamAuthSession: StreamAuthSessionUseCase,
    private val observeChatPartnerIds: ObserveChatPartnerIdsUseCase,
    private val fetchChatListPage: FetchChatListPageUseCase,
    private val fetchUsersByIds: FetchUsersByIdsUseCase,
) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 20
    }

    private val _state = MutableStateFlow(ConversationsUiState())
    val state: StateFlow<ConversationsUiState> = _state.asStateFlow()

    private val listRetry = MutableStateFlow(0)
    private var nextEndBefore: Pair<String, String>? = null

    init {
        viewModelScope.launch {
            combine(
                streamAuthSession(),
                listRetry,
            ) { auth, _ -> auth?.uid }
                .collectLatest { uid ->
                    if (uid == null) {
                        nextEndBefore = null
                        _state.value = ConversationsUiState(isInitialLoading = false)
                        return@collectLatest
                    }
                    _state.update { it.copy(currentUserId = uid) }
                    observeChatPartnerIds(uid)
                        .debounce(400L)
                        .catch { e ->
                            _state.update {
                                it.copy(
                                    listError = e.userFacingMessage(
                                        offlineFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                                        genericFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                                    ),
                                )
                            }
                            emit(emptyList())
                        }
                        .collectLatest {
                            loadFirstChatPage(uid)
                        }
                }
        }
    }

    /**
     *
     * @param event User intent.
     * @author udit
     */
    fun onEvent(event: ConversationsUiEvent) {
        when (event) {
            ConversationsUiEvent.LoadMore -> loadMoreChats()
            ConversationsUiEvent.RetryList -> {
                _state.update { it.copy(listError = null) }
                listRetry.update { it + 1 }
            }
        }
    }

    private suspend fun loadFirstChatPage(uid: String) {
        _state.update {
            it.copy(isInitialLoading = true, isLoadingMore = false, listError = null)
        }
        nextEndBefore = null
        fetchChatListPage(uid, PAGE_SIZE, null, null).fold(
            onSuccess = { page ->
                nextEndBefore = page.nextPageEndBeforeTimestamp?.let { t ->
                    page.nextPageEndBeforeKey?.let { k -> t to k }
                }
                _state.update { it.copy(hasMore = page.hasMore) }
                resolveAndSetPartners(page.entries, replaceAll = true)
                _state.update { it.copy(isInitialLoading = false) }
            },
            onFailure = { e ->
                _state.update {
                    it.copy(
                        listError = e.userFacingMessage(
                            offlineFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                            genericFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                        ),
                        partners = emptyList(),
                        hasMore = false,
                        isInitialLoading = false,
                    )
                }
            },
        )
    }

    private suspend fun resolveAndSetPartners(entries: List<ChatListEntry>, replaceAll: Boolean) {
        val ids = entries.map { it.partnerId }
        if (ids.isEmpty()) {
            if (replaceAll) _state.update { it.copy(partners = emptyList()) }
            return
        }
        fetchUsersByIds(ids).fold(
            onSuccess = { users ->
                val byId = users.associateBy { it.id }
                val ordered = ids.mapNotNull { byId[it] }
                _state.update { s ->
                    if (replaceAll) {
                        s.copy(partners = ordered)
                    } else {
                        val seen = s.partners.map { it.id }.toMutableSet()
                        s.copy(partners = s.partners + ordered.filter { seen.add(it.id) })
                    }
                }
            },
            onFailure = { e ->
                _state.update { s ->
                    s.copy(
                        listError = s.listError ?: e.userFacingMessage(
                            offlineFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                            genericFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                        ),
                        partners = if (replaceAll) emptyList() else s.partners,
                    )
                }
            },
        )
    }

    private fun loadMoreChats() {
        val s = _state.value
        val uid = s.currentUserId ?: return
        if (!s.hasMore || s.isLoadingMore || s.isInitialLoading) return
        val cursor = nextEndBefore ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            fetchChatListPage(uid, PAGE_SIZE, cursor.first, cursor.second).fold(
                onSuccess = { page ->
                    nextEndBefore = page.nextPageEndBeforeTimestamp?.let { t ->
                        page.nextPageEndBeforeKey?.let { k -> t to k }
                    }
                    _state.update { it.copy(hasMore = page.hasMore) }
                    resolveAndSetPartners(page.entries, replaceAll = false)
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            listError = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                                genericFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                            ),
                        )
                    }
                },
            )
            _state.update { it.copy(isLoadingMore = false) }
        }
    }
}
