package com.example.whizzz.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Koin-provided [NetworkConnectivity]: exposes validated internet as a [StateFlow] via [ConnectivityManager].
 *
 * @param context Application context from [org.koin.android.ext.koin.androidContext].
 * @author udit
 */
class NetworkConnectivityImpl(
    context: Context,
) : NetworkConnectivity {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _isOnline = MutableStateFlow(networkIsValidated(cm))

    /**
     * Hot state of validated internet reachability.
     * @author udit
     */
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                /**
                 * Re-evaluates connectivity when a network becomes available.
                 *
                 * @param network The network that triggered the callback.
                 * @author udit
                 */
                override fun onAvailable(network: Network) {
                    _isOnline.value = networkIsValidated(cm)
                }

                /**
                 * Re-evaluates connectivity when a network is lost.
                 *
                 * @param network The network that was lost.
                 * @author udit
                 */
                override fun onLost(network: Network) {
                    _isOnline.value = networkIsValidated(cm)
                }

                /**
                 * Updates online state from capability flags (internet + validated).
                 *
                 * @param network The network whose capabilities changed.
                 * @param caps New capabilities for [network].
                 * @author udit
                 */
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    _isOnline.value = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            },
        )
    }

    /**
     * Reads the default network and checks internet + validated capabilities.
     *
     * @param manager System connectivity service.
     * @return `true` when the active default network is validated for internet.
     * @author udit
     */
    private fun networkIsValidated(manager: ConnectivityManager): Boolean {
        val network = manager.activeNetwork ?: return false
        val caps = manager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
