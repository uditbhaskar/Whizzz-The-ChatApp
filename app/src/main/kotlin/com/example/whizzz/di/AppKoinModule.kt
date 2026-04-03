package com.example.whizzz.di

import com.example.whizzz.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for app-level [androidx.lifecycle.ViewModel] instances (e.g. splash coordinated with [com.example.whizzz.MainActivity]).
 * @author udit
 */
val appModule = module {
    viewModelOf(::SplashViewModel)
}
