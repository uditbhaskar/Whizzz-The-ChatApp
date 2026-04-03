package com.example.whizzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.whizzz.navigation.WhizzzApp
import com.example.whizzz.splash.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModel()

    /**
     * Installs splash retention, enables edge-to-edge, and sets [WhizzzApp] as content.
     *
     * @param savedInstanceState Activity restored state, if any.
     * @author udit
     */
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
