package com.example.whizzz.data.di

import com.example.whizzz.data.connectivity.NetworkConnectivityImpl
import com.example.whizzz.data.repository.firebase.AuthRepositoryImpl
import com.example.whizzz.data.repository.firebase.ChatRepositoryImpl
import com.example.whizzz.data.repository.firebase.FcmTokenRepositoryImpl
import com.example.whizzz.data.repository.firebase.UserRepositoryImpl
import com.example.whizzz.data.local.OpenChatStore
import com.example.whizzz.data.push.FirebasePushTokenReader
import com.example.whizzz.domain.connectivity.NetworkConnectivity
import com.example.whizzz.domain.repository.AuthRepository
import com.example.whizzz.domain.repository.ChatRepository
import com.example.whizzz.domain.repository.FcmTokenRepository
import com.example.whizzz.domain.repository.OpenChatTracker
import com.example.whizzz.domain.repository.UserRepository
import com.example.whizzz.domain.usecase.push.DevicePushTokenReader
import com.example.whizzz.domain.usecase.chat.FetchChatListPageUseCase
import com.example.whizzz.domain.usecase.user.FetchUsersByIdsUseCase
import com.example.whizzz.domain.usecase.user.FetchUsersDirectoryPageUseCase
import com.example.whizzz.domain.usecase.chat.MarkPeerMessagesSeenUseCase
import com.example.whizzz.domain.usecase.chat.ObserveChatPartnerIdsUseCase
import com.example.whizzz.domain.usecase.chat.ObserveConversationMessagesUseCase
import com.example.whizzz.domain.usecase.connectivity.ObserveNetworkOnlineUseCase
import com.example.whizzz.domain.usecase.user.ObserveUserProfileUseCase
import com.example.whizzz.domain.usecase.push.RegisterPushTokenUseCase
import com.example.whizzz.domain.usecase.chat.SendConversationMessageUseCase
import com.example.whizzz.domain.usecase.chat.SetActiveChatPeerUseCase
import com.example.whizzz.domain.usecase.presence.SetUserPresenceUseCase
import com.example.whizzz.domain.usecase.auth.SendPasswordResetUseCase
import com.example.whizzz.domain.usecase.auth.SignInUseCase
import com.example.whizzz.domain.usecase.auth.SignOutUseCase
import com.example.whizzz.domain.usecase.auth.SignUpUseCase
import com.example.whizzz.domain.usecase.auth.StreamAuthSessionUseCase
import com.example.whizzz.domain.usecase.user.ObserveCurrentUserUseCase
import com.example.whizzz.domain.usecase.user.UpdateBioUseCase
import com.example.whizzz.domain.usecase.user.UpdateUsernameUseCase
import com.example.whizzz.domain.usecase.user.UploadProfileImageUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Koin module: Firebase singletons, [com.example.whizzz.data.repository.firebase] implementations,
 * connectivity, [com.example.whizzz.data.local.OpenChatStore], domain **use cases**, and push token reader.
 * @author udit
 */
val dataModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single<NetworkConnectivity> { NetworkConnectivityImpl(androidContext()) }
    single { OpenChatStore(androidContext()) }
    single<OpenChatTracker> { get<OpenChatStore>() }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<FcmTokenRepository> { FcmTokenRepositoryImpl(get()) }

    single<DevicePushTokenReader> { FirebasePushTokenReader() }

    factoryOf(::StreamAuthSessionUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::SignUpUseCase)
    factoryOf(::SendPasswordResetUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::ObserveCurrentUserUseCase)
    factoryOf(::UpdateUsernameUseCase)
    factoryOf(::UpdateBioUseCase)
    factoryOf(::UploadProfileImageUseCase)
    factoryOf(::ObserveNetworkOnlineUseCase)
    factoryOf(::RegisterPushTokenUseCase)
    factoryOf(::SetUserPresenceUseCase)
    factoryOf(::FetchUsersDirectoryPageUseCase)
    factoryOf(::ObserveChatPartnerIdsUseCase)
    factoryOf(::FetchChatListPageUseCase)
    factoryOf(::FetchUsersByIdsUseCase)
    factoryOf(::ObserveConversationMessagesUseCase)
    factoryOf(::ObserveUserProfileUseCase)
    factoryOf(::SendConversationMessageUseCase)
    factoryOf(::MarkPeerMessagesSeenUseCase)
    factoryOf(::SetActiveChatPeerUseCase)
}
