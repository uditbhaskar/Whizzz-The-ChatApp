package com.example.whizzz.splash

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.whizzz.core.common.navigation.WhizzzRoutes
import com.example.whizzz.core.ui.theme.WhizzzTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Splash route: reads initial auth destination, navigates away, then clears the splash hold for the activity splash API.
 *
 * @param navController [NavController] used to replace the splash entry with login or home.
 * @param viewModel Resolves the start route ([SplashViewModel.resolveStartRoute]) and splash visibility ([SplashViewModel.keepSplashScreen]).
 * @author udit
 */
@Composable
fun SplashRoute(
    navController: NavController,
    viewModel: SplashViewModel = koinViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity),
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

/**
 * Static Compose preview approximating splash branding without navigation or [SplashViewModel].
 * @author udit
 */
@Preview(showBackground = true, showSystemUi = false, name = "Splash (static)")
@Composable
private fun SplashRouteStaticPreview() {
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Text("Whizzz", color = Color.White)
        }
    }
}
