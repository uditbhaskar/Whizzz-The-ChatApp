package com.example.whizzz.domain.connectivity

import kotlinx.coroutines.flow.StateFlow

/**
 * Observes whether the device has a validated internet connection.
 *
 * @author udit
 */
interface NetworkConnectivity {
    val isOnline: StateFlow<Boolean>
}
