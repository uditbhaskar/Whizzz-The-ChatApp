package com.example.whizzz.core.common.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

private const val WHILE_SUBSCRIBED_STOP_TIMEOUT_MS: Long = 5_000L

/**
 * [stateIn] with [SharingStarted.WhileSubscribed] (5s stop timeout) — standard pattern for UI-bound [StateFlow].
 *
 * @receiver Cold [Flow] to expose as hot UI state.
 * @param scope Usually viewModelScope.
 * @param initialValue Emitted until the first real value arrives.
 * @return [StateFlow] shared while collectors are subscribed.
 * @author udit
 */
fun <T> Flow<T>.stateInWhileSubscribed(
    scope: CoroutineScope,
    initialValue: T,
): StateFlow<T> = stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_STOP_TIMEOUT_MS),
    initialValue = initialValue,
)
