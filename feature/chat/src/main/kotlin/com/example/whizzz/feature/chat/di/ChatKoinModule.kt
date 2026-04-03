package com.example.whizzz.feature.chat.di

import androidx.lifecycle.SavedStateHandle
import com.example.whizzz.feature.chat.presentation.ConversationViewModel
import com.example.whizzz.feature.chat.presentation.PeerProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for chat feature [androidx.lifecycle.ViewModel] instances (navigation [SavedStateHandle] args).
 *
 * ViewModels depend on domain **use cases** registered in dataModule.
 * @author udit
 */
val chatFeatureModule = module {
    viewModel { (handle: SavedStateHandle) ->
        ConversationViewModel(
            savedStateHandle = handle,
            streamAuthSession = get(),
            observeConversationMessages = get(),
            observeUserProfile = get(),
            sendConversationMessage = get(),
            markPeerMessagesSeen = get(),
            setActiveChatPeer = get(),
            observeNetworkOnline = get(),
        )
    }
    viewModel { (handle: SavedStateHandle) ->
        PeerProfileViewModel(
            savedStateHandle = handle,
            observeUserProfile = get(),
        )
    }
}
