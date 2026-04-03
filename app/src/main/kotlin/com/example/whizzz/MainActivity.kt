package com.example.whizzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.whizzz.navigation.WhizzzApp
import com.example.whizzz.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host for Compose navigation.
 *
 * @author udit
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { splashViewModel.keepSplashScreen.value }
        setContent {
            WhizzzApp()
        }
    }
}
