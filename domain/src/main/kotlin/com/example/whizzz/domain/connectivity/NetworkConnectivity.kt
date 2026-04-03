package com.example.whizzz.domain.connectivity

import kotlinx.coroutines.flow.StateFlow

/**
 * Domain port: validated internet reachability (implementation in the data layer).
 *
 * @property isOnline Hot [StateFlow]; `true` when the device is considered online.
 * @author udit
 */
interface NetworkConnectivity {
    val isOnline: StateFlow<Boolean>
}
