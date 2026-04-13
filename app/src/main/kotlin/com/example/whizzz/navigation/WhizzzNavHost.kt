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
import com.example.whizzz.feature.auth.ui.forgot.ForgotRoute
import com.example.whizzz.feature.auth.ui.login.LoginRoute
import com.example.whizzz.feature.auth.ui.register.RegisterRoute
import com.example.whizzz.feature.chat.ui.ConversationRoute
import com.example.whizzz.feature.chat.ui.PeerProfileRoute
import com.example.whizzz.feature.home.ui.home.HomeRoute
import com.example.whizzz.presence.AppProcessPresenceEffect
import com.example.whizzz.splash.SplashRoute

/**
 * Root Compose entry: applies [AppTheme] and hosts [WhizzzNavHost]. No parameters.
 *
 * @author udit
 */
@Composable
fun WhizzzApp() {
    AppTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        AppProcessPresenceEffect()
        WhizzzNavHost(modifier = Modifier.fillMaxSize())
    }
}

/**
 * [NavHost] for splash, auth, home shell, chat, and peer profile destinations.
 *
 * @param modifier Modifier applied to the [NavHost].
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
        composable(
            route = WhizzzRoutes.HOME,
            exitTransition = { homeExitTransition() },
            popEnterTransition = { homePopEnterTransition() },
        ) {
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
            enterTransition = { chatEnterTransition() },
            exitTransition = { chatExitTransition() },
            popEnterTransition = { chatPopEnterTransition() },
            popExitTransition = { chatPopExitTransition() },
        ) {
            ConversationRoute(
                onBack = { navController.popBackStack() },
                onOpenPeerProfile = { userId ->
                    navController.navigate(WhizzzRoutes.peerProfile(userId))
                },
            )
        }
        composable(
            route = WhizzzRoutes.PEER_PROFILE_PATTERN,
            arguments = listOf(
                navArgument(WhizzzStrings.Nav.ARG_PROFILE_USER_ID) { type = NavType.StringType },
            ),
            enterTransition = { peerProfileEnterTransition() },
            popExitTransition = { peerProfilePopExitTransition() },
        ) {
            PeerProfileRoute(onBack = { navController.popBackStack() })
        }
    }
}
