package com.example.whizzz.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Fullscreen profile photo with open/close (fade + scale) around [ProfilePhotoViewerOverlay].
 *
 * @param visible Whether the overlay participates in the composition and animation.
 * @param imageUrl Profile image URL, data URI, or default token understood by [WhizzzProfileAvatar].
 * @param title Toolbar title (e.g. display name).
 * @param onDismiss Invoked when the user closes the overlay.
 * @param onEditPhoto Optional; when set, toolbar offers edit to change the photo (own profile).
 * @param modifier Optional outer [Modifier] for the animated container.
 * @author udit
 */
@Composable
fun AnimatedProfilePhotoViewerOverlay(
    visible: Boolean,
    imageUrl: String,
    title: String,
    onDismiss: () -> Unit,
    onEditPhoto: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier.fillMaxSize(),
        enter = fadeIn(animationSpec = tween(240)) +
            scaleIn(initialScale = 0.88f, animationSpec = tween(280)),
        exit = fadeOut(animationSpec = tween(200)) +
            scaleOut(targetScale = 0.92f, animationSpec = tween(240)),
    ) {
        ProfilePhotoViewerOverlay(
            imageUrl = imageUrl,
            title = title,
            onDismiss = onDismiss,
            onEditPhoto = onEditPhoto,
        )
    }
}
