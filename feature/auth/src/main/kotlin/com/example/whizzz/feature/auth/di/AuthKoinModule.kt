package com.example.whizzz.feature.auth.di

import com.example.whizzz.feature.auth.presentation.forgot.ForgotViewModel
import com.example.whizzz.feature.auth.presentation.login.LoginViewModel
import com.example.whizzz.feature.auth.presentation.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for the auth feature.
 *
 * Binds ViewModels under [com.example.whizzz.feature.auth.presentation]; Compose entry points live under
 * [com.example.whizzz.feature.auth.ui].
 *
 * @author udit
 */
val authFeatureModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ForgotViewModel)
}
