package com.example.whizzz.core.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Bottom inset for the software keyboard (IME). Use inside Scaffold body when navigation bar padding is already applied.
 *
 * @receiver Modifier chain receiving IME padding.
 * @return Modifier with WindowInsets.ime padding applied.
 * @author udit
 */
@Composable
fun Modifier.whizzzImeInsetPadding(): Modifier =
    windowInsetsPadding(WindowInsets.ime)

/**
 * Navigation bar plus IME so bottom bars and full-screen forms stay above the keyboard and gesture nav.
 *
 * @receiver Modifier chain receiving combined bottom insets.
 * @return Modifier with navigation bar and IME union padding.
 * @author udit
 */
@Composable
fun Modifier.whizzzKeyboardInsetPadding(): Modifier =
    windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
