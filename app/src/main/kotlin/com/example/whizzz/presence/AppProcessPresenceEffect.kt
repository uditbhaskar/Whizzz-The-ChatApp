package com.example.whizzz.presence

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.usecase.presence.SetUserPresenceUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Syncs Realtime Database presence with **app foreground** using [ProcessLifecycleOwner]:
 * online on [Lifecycle.Event.ON_START], offline on [Lifecycle.Event.ON_STOP].
 *
 * This runs for the whole process (not per Composable destination), so status updates as soon as
 * the user leaves the app (home/recents/another app). [SetUserPresenceUseCase] no-ops when signed out.
 */
@Composable
fun AppProcessPresenceEffect() {
    val setPresence: SetUserPresenceUseCase = koinInject()
    val scope = rememberCoroutineScope()
    DisposableEffect(setPresence) {
        val process = ProcessLifecycleOwner.get()
        fun syncFromCurrentState() {
            scope.launch {
                if (process.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    setPresence(WhizzzStrings.Defaults.PRESENCE_ONLINE)
                } else {
                    setPresence(WhizzzStrings.Defaults.STATUS_OFFLINE)
                }
            }
        }
        syncFromCurrentState()
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    scope.launch {
                        setPresence(WhizzzStrings.Defaults.PRESENCE_ONLINE)
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    scope.launch {
                        setPresence(WhizzzStrings.Defaults.STATUS_OFFLINE)
                    }
                }
                else -> {}
            }
        }
        process.lifecycle.addObserver(observer)
        onDispose {
            process.lifecycle.removeObserver(observer)
        }
    }
}
