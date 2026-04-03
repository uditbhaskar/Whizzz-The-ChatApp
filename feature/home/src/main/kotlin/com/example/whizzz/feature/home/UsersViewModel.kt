package com.example.whizzz.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.coroutines.stateInWhileSubscribed
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.repository.AuthRepository
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
import javax.inject.Inject

/**
 * User directory tab with live search.
 *
 * @author udit
 */
@HiltViewModel
class UsersViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _listRetry = MutableStateFlow(0)
    private val _listError = MutableStateFlow<String?>(null)
    val listError: StateFlow<String?> = _listError.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val users: StateFlow<List<User>> = combine(
        authRepository.authState(),
        _searchQuery,
        _listRetry,
    ) { auth, q, _ -> auth?.uid to q }
        .flatMapLatest { (uid, q) ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                userRepository.searchUsers(q.trim().lowercase())
                    .map { list -> list.filter { it.id != uid } }
                    .catch { e ->
                        _listError.value = e.userFacingMessage(
                            offlineFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                            genericFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                        )
                        emit(emptyList())
                    }
            }
        }
        .stateInWhileSubscribed(
            scope = viewModelScope,
            initialValue = emptyList(),
        )

    /**
     * @author udit
     */
    fun onSearchChange(value: String) {
        _searchQuery.update { value }
    }

    fun retryUsersList() {
        _listError.value = null
        _listRetry.update { it + 1 }
    }
}
