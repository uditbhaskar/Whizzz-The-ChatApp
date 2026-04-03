/**
 * Application navigation: themed root, [NavHost] routes, and deep links into chat.
 *
 * @author udit
 */
package com.example.whizzz.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.whizzz.core.common.navigation.WhizzzRoutes
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.theme.WhizzzTheme as AppTheme
import com.example.whizzz.feature.auth.forgot.ForgotRoute
import com.example.whizzz.feature.auth.login.LoginRoute
import com.example.whizzz.feature.auth.register.RegisterRoute
import com.example.whizzz.feature.chat.ConversationRoute
import com.example.whizzz.feature.home.HomeRoute
import com.example.whizzz.splash.SplashRoute

/**
 * Root composable: applies app theme and shows [WhizzzNavHost].
 *
 * @author udit
 */
@Composable
fun WhizzzApp() {
    AppTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        WhizzzNavHost(modifier = Modifier.fillMaxSize())
    }
}

/**
 * [NavHost] for splash, auth, home shell, and one-to-one chat.
 *
 * @author udit
 */
@Composable
fun WhizzzNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = WhizzzRoutes.SPLASH,
        modifier = modifier,
    ) {
        composable(WhizzzRoutes.SPLASH) {
            SplashRoute(navController = navController)
        }
        composable(WhizzzRoutes.LOGIN) {
            LoginRoute(
                onSuccess = {
                    navController.navigate(WhizzzRoutes.HOME) {
                        popUpTo(WhizzzRoutes.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegister = { navController.navigate(WhizzzRoutes.REGISTER) },
                onForgot = { navController.navigate(WhizzzRoutes.FORGOT) },
            )
        }
        composable(WhizzzRoutes.REGISTER) {
            RegisterRoute(
                onSuccess = {
                    navController.navigate(WhizzzRoutes.HOME) {
                        popUpTo(WhizzzRoutes.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLogin = { navController.popBackStack() },
            )
        }
        composable(WhizzzRoutes.FORGOT) {
            ForgotRoute(onBack = { navController.popBackStack() })
        }
        composable(WhizzzRoutes.HOME) {
            HomeRoute(
                onSignOut = {
                    navController.navigate(WhizzzRoutes.LOGIN) {
                        popUpTo(WhizzzRoutes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenChat = { peerId ->
                    navController.navigate(WhizzzRoutes.chat(peerId))
                },
            )
        }
        composable(
            route = WhizzzRoutes.CHAT_PATTERN,
            arguments = listOf(
                navArgument(WhizzzStrings.Nav.ARG_PEER_ID) { type = NavType.StringType },
            ),
        ) {
            ConversationRoute(onBack = { navController.popBackStack() })
        }
    }
}
