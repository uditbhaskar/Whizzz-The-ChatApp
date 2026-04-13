package com.example.whizzz.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.example.whizzz.core.common.navigation.WhizzzRoutes
import com.example.whizzz.core.strings.WhizzzStrings
import kotlin.math.roundToInt
private const val NAV_DURATION_MS = 280

/**
 * Whether [route] denotes the one-to-one chat destination (ignoring query params).
 *
 * @param route Raw destination route string.
 * @return True when the route is under the chat prefix.
 * @author udit
 */
private fun isChatRoute(route: String?): Boolean =
    route?.substringBefore('?')
        ?.startsWith("${WhizzzStrings.Nav.CHAT_PREFIX}/") == true

/**
 * Whether [route] denotes the peer profile destination (ignoring query params).
 *
 * @param route Raw destination route string.
 * @return True when the route is under the peer profile prefix.
 * @author udit
 */
private fun isPeerProfileRoute(route: String?): Boolean =
    route?.substringBefore('?')
        ?.startsWith("${WhizzzStrings.Nav.PEER_PROFILE_PREFIX}/") == true

/**
 * Enter transition sliding the incoming screen in from the right with a light fade.
 *
 * @return Combined slide + fade [androidx.compose.animation.EnterTransition].
 * @author udit
 */
internal fun slideInFromRightWithFade() =
    slideInHorizontally(animationSpec = tween(NAV_DURATION_MS)) { fullWidth -> fullWidth } +
        fadeIn(animationSpec = tween(NAV_DURATION_MS), initialAlpha = 0.94f)

/**
 * Exit transition that moves the outgoing screen slightly left while fading (stacked look).
 *
 * @return Combined slide + fade exit transition.
 * @author udit
 */
internal fun slideOutToLeftBehindWithFade() =
    slideOutHorizontally(animationSpec = tween(NAV_DURATION_MS)) { fullWidth ->
        -(fullWidth * 0.12f).roundToInt()
    } + fadeOut(animationSpec = tween(NAV_DURATION_MS), targetAlpha = 0.94f)

/**
 * Exit transition sliding the screen fully off to the right.
 *
 * @return Combined slide + fade exit transition.
 * @author udit
 */
internal fun slideOutToRightWithFade() =
    slideOutHorizontally(animationSpec = tween(NAV_DURATION_MS)) { fullWidth -> fullWidth } +
        fadeOut(animationSpec = tween(NAV_DURATION_MS))

/**
 * Enter transition for a screen returning from the right stack (slight left offset + fade).
 *
 * @return Combined slide + fade enter transition.
 * @author udit
 */
internal fun slideInFromLeftBehindWithFade() =
    slideInHorizontally(animationSpec = tween(NAV_DURATION_MS)) { fullWidth ->
        -(fullWidth * 0.12f).roundToInt()
    } + fadeIn(animationSpec = tween(NAV_DURATION_MS), initialAlpha = 0.94f)

/**
 * Home exit when pushing chat: leaves home slightly visible behind.
 *
 * @return Custom exit or null for the default transition.
 * @author udit
 */
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.homeExitTransition() =
    if (isChatRoute(targetState.destination.route)) {
        slideOutToLeftBehindWithFade()
    } else {
        null
    }

/**
 * Home pop-enter when returning from chat.
 *
 * @return Custom enter or null for the default transition.
 * @author udit
 */
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.homePopEnterTransition() =
    if (isChatRoute(initialState.destination.route)) {
        slideInFromLeftBehindWithFade()
    } else {
        null
    }

/**
 * Chat enter from home or when popping peer profile back to chat.
 *
 * @return Custom enter or null for the default transition.
 * @author udit
 */
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.chatEnterTransition() =
    when {
        initialState.destination.route == WhizzzRoutes.HOME -> slideInFromRightWithFade()
        isPeerProfileRoute(initialState.destination.route) -> slideInFromRightWithFade()
        else -> null
    }

/**
 * Chat exit when opening peer profile (home stays slightly behind).
 *
 * @return Custom exit or null for the default transition.
 * @author udit
 */
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.chatExitTransition() =
    if (isPeerProfileRoute(targetState.destination.route)) {
        slideOutToLeftBehindWithFade()
    } else {
        null
    }

/**
 * Chat pop-enter when returning from peer profile.
 *
 * @return Custom enter or null for the default transition.
 * @author udit
 */
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.chatPopEnterTransition() =
    if (isPeerProfileRoute(initialState.destination.route)) {
        slideInFromLeftBehindWithFade()
    } else {
        null
    }

/**
 * Chat pop-exit when navigating back to home.
 *
 * @return Custom exit or null for the default transition.
 * @author udit
 */
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.chatPopExitTransition() =
    if (targetState.destination.route == WhizzzRoutes.HOME) {
        slideOutToRightWithFade()
    } else {
        null
    }

/**
 * Peer profile enter when pushed from chat.
 *
 * @return Custom enter or null for the default transition.
 * @author udit
 */
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.peerProfileEnterTransition() =
    if (isChatRoute(initialState.destination.route)) {
        slideInFromRightWithFade()
    } else {
        null
    }

/**
 * Peer profile pop-exit back to chat (slide off to the right).
 *
 * @return Exit transition sliding out to the right.
 * @author udit
 */
internal fun peerProfilePopExitTransition() =
    slideOutToRightWithFade()
