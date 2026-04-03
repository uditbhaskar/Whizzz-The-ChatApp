package com.example.whizzz.data.di

import com.example.whizzz.data.firebase.AuthRepositoryImpl
import com.example.whizzz.data.firebase.ChatRepositoryImpl
import com.example.whizzz.data.firebase.FcmTokenRepositoryImpl
import com.example.whizzz.data.firebase.UserRepositoryImpl
import com.example.whizzz.domain.repository.AuthRepository
import com.example.whizzz.domain.repository.ChatRepository
import com.example.whizzz.domain.repository.FcmTokenRepository
import com.example.whizzz.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds domain repository contracts to data-layer implementations.
 *
 * @author udit
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindFcmTokenRepository(impl: FcmTokenRepositoryImpl): FcmTokenRepository
}
