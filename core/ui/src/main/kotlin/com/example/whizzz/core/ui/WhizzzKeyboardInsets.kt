/**
 * Compose [Modifier] extensions for IME and navigation bar window insets (keyboard-safe layouts).
 *
 * @author udit
 */
package com.example.whizzz.core.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Bottom inset for the software keyboard (IME). Use inside [androidx.compose.material3.Scaffold]
 * body when the scaffold already applied navigation bar padding to content.
 */
@Composable
fun Modifier.whizzzImeInsetPadding(): Modifier =
    windowInsetsPadding(WindowInsets.ime)

/**
 * Navigation bar + IME so bottom bars and full-screen forms stay above the keyboard and gesture nav.
 */
@Composable
fun Modifier.whizzzKeyboardInsetPadding(): Modifier =
    windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
