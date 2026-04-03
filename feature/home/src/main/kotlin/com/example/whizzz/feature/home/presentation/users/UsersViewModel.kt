package com.example.whizzz.feature.home.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.usecase.user.FetchUsersDirectoryPageUseCase
import com.example.whizzz.domain.usecase.auth.StreamAuthSessionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * MVI [androidx.lifecycle.ViewModel] for the Users tab: [state] + [onEvent].
 *
 * Orchestrates [StreamAuthSessionUseCase] and [FetchUsersDirectoryPageUseCase] (Clean Architecture: no direct repository).
 *
 *
 * @param streamAuthSession Signed-in session stream.
 * @param fetchUsersDirectoryPage Paged directory port.
 * @author udit
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModel(
    streamAuthSession: StreamAuthSessionUseCase,
    private val fetchUsersDirectoryPage: FetchUsersDirectoryPageUseCase,
) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 20
        const val SEARCH_DEBOUNCE_MS = 100L
    }

    private val _state = MutableStateFlow(UsersUiState())
    val state: StateFlow<UsersUiState> = _state.asStateFlow()

    private val listRetry = MutableStateFlow(0)
    private var searchDebounceJob: Job? = null

    init {
        viewModelScope.launch {
            streamAuthSession()
                .flatMapLatest { auth ->
                    val uid = auth?.uid
                    if (uid == null) {
                        flowOf(Triple<String?, String, Int>(null, "", 0))
                    } else {
                        combine(
                            _state.map { it.committedSearchQuery }.distinctUntilChanged(),
                            listRetry,
                        ) { committed, retry ->
                            Triple(uid, committed, retry)
                        }
                    }
                }
                .collectLatest { triple ->
                    val uid = triple.first
                    if (uid == null) {
                        searchDebounceJob?.cancel()
                        searchDebounceJob = null
                        _state.value = UsersUiState()
                        return@collectLatest
                    }
                    _state.update { it.copy(currentUserId = uid) }
                    val prefix = triple.second.trim().lowercase()
                    loadFirstPage(uid, prefix)
                }
        }
    }

    private suspend fun loadFirstPage(uid: String, prefix: String) {
        _state.update {
            it.copy(
                isInitialLoading = true,
                isLoadingMore = false,
                listError = null,
                nextCursor = null,
            )
        }
        try {
            fetchUsersDirectoryPage(prefix, PAGE_SIZE, null, uid).fold(
                onSuccess = { page ->
                    _state.update {
                        it.copy(
                            users = page.users,
                            nextCursor = page.nextCursor,
                            hasMore = page.hasMore,
                            lastLoadedPrefix = prefix,
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            listError = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                                genericFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                            ),
                            users = emptyList(),
                            hasMore = false,
                        )
                    }
                },
            )
        } finally {
            _state.update { it.copy(isInitialLoading = false) }
        }
    }

    /**
     *
     * @param event User intent to reduce into [state] or trigger loads.
     * @author udit
     */
    fun onEvent(event: UsersUiEvent) {
        when (event) {
            is UsersUiEvent.SearchFieldChanged -> onSearchFieldChanged(event.value)
            UsersUiEvent.LoadMore -> loadMoreUsers()
            UsersUiEvent.RetryList -> {
                _state.update { it.copy(listError = null) }
                listRetry.update { it + 1 }
            }
        }
    }

    private fun onSearchFieldChanged(value: String) {
        _state.update { it.copy(searchFieldText = value) }
        searchDebounceJob?.cancel()
        searchDebounceJob = null
        if (value.isBlank()) {
            _state.update { it.copy(committedSearchQuery = "") }
            return
        }
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            if (isActive) {
                _state.update { it.copy(committedSearchQuery = value) }
            }
        }
    }

    private fun loadMoreUsers() {
        val s = _state.value
        val uid = s.currentUserId ?: return
        if (!s.hasMore || s.isLoadingMore || s.isInitialLoading) return
        val c = s.nextCursor ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            try {
                fetchUsersDirectoryPage(s.lastLoadedPrefix, PAGE_SIZE, c, uid).fold(
                    onSuccess = { page ->
                        _state.update {
                            it.copy(
                                users = it.users + page.users,
                                nextCursor = page.nextCursor,
                                hasMore = page.hasMore,
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                listError = e.userFacingMessage(
                                    offlineFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                                    genericFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                                ),
                            )
                        }
                    },
                )
            } finally {
                _state.update { it.copy(isLoadingMore = false) }
            }
        }
    }
}
