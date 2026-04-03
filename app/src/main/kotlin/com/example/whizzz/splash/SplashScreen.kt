package com.example.whizzz.splash

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.whizzz.core.common.navigation.WhizzzRoutes

/**
 * Auth routing gate only: artwork + spinner live on the system [androidx.core.splashscreen.SplashScreen].
 *
 * @author udit
 */
@Composable
fun SplashRoute(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    Box(Modifier.fillMaxSize())
    LaunchedEffect(Unit) {
        val dest = viewModel.resolveStartRoute()
        navController.navigate(dest) {
            popUpTo(WhizzzRoutes.SPLASH) { inclusive = true }
            launchSingleTop = true
        }
        viewModel.dismissSplashScreen()
    }
}
