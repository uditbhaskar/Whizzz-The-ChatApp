/**
 * [kotlinx.coroutines.flow.Flow] utilities for ViewModel-driven UI ([StateFlow] with while-subscribed sharing).
 *
 * @author udit
 */
package com.example.whizzz.core.common.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Stop timeout (ms) after the last subscriber disappears before upstream collection stops.
 *
 * @author udit
 */
const val WHILE_SUBSCRIBED_STOP_TIMEOUT_MS: Long = 5_000L

/**
 * [stateIn] with [SharingStarted.WhileSubscribed] — standard pattern for UI-bound [StateFlow].
 *
 * @param scope Usually viewModelScope.
 * @param initialValue Emitted until the first real value arrives.
 *
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
