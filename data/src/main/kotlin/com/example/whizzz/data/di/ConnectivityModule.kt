package com.example.whizzz.data.di

import com.example.whizzz.data.connectivity.NetworkConnectivityImpl
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Network reachability for offline messaging in ViewModels.
 *
 * @author udit
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {

    @Binds
    @Singleton
    abstract fun bindNetworkConnectivity(impl: NetworkConnectivityImpl): NetworkConnectivity
}
