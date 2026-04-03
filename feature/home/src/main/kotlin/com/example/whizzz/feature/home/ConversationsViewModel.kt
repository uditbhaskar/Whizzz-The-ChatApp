package com.example.whizzz.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whizzz.core.common.coroutines.stateInWhileSubscribed
import com.example.whizzz.core.common.errors.userFacingMessage
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.repository.AuthRepository
import com.example.whizzz.domain.repository.ChatRepository
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
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Chat list tab: partners from ChatList joined with [User] rows.
 *
 * @author udit
 */
@HiltViewModel
class ConversationsViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _listRetry = MutableStateFlow(0)
    private val _listError = MutableStateFlow<String?>(null)
    val listError: StateFlow<String?> = _listError.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val partners: StateFlow<List<User>> = combine(
        authRepository.authState(),
        _listRetry,
    ) { auth, _ -> auth?.uid }
        .flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                combine(
                    chatRepository.observeChatPartnerIds(uid)
                        .catch { e ->
                            _listError.value = e.userFacingMessage(
                                offlineFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                                genericFallback = WhizzzStrings.Errors.LOAD_CHATS_FAILED,
                            )
                            emit(emptyList())
                        },
                    userRepository.observeAllUsersOrdered()
                        .catch { e ->
                            _listError.update { existing ->
                                existing ?: e.userFacingMessage(
                                    offlineFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                                    genericFallback = WhizzzStrings.Errors.LOAD_PEOPLE_FAILED,
                                )
                            }
                            emit(emptyList())
                        },
                ) { partnerIds, all ->
                    partnerIds.mapNotNull { id -> all.find { it.id == id } }
                }
            }
        }
        .stateInWhileSubscribed(
            scope = viewModelScope,
            initialValue = emptyList(),
        )

    fun retryPartnersList() {
        _listError.value = null
        _listRetry.update { it + 1 }
    }
}
