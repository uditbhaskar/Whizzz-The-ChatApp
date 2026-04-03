package com.example.whizzz.data.di

import com.example.whizzz.data.prefs.OpenChatStore
import com.example.whizzz.domain.repository.OpenChatTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds preference-backed helpers to domain interfaces.
 *
 * @author udit
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PrefsModule {

    @Binds
    @Singleton
    abstract fun bindOpenChatTracker(impl: OpenChatStore): OpenChatTracker
}
