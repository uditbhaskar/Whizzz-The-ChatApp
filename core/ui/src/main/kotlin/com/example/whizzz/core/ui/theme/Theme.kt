package com.example.whizzz.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val WhizzzBrandDarkScheme = darkColorScheme(
    primary = WhizzzAccent,
    onPrimary = Color.Black,
    primaryContainer = WhizzzSurfaceMuted,
    onPrimaryContainer = Color.White,
    secondary = WhizzzAccentSecondary,
    onSecondary = Color.Black,
    tertiary = WhizzzAccent,
    background = WhizzzScreenBackground,
    onBackground = Color.White,
    surface = WhizzzScreenBackground,
    onSurface = Color.White,
    surfaceVariant = WhizzzSurfaceMuted,
    onSurfaceVariant = Color(0xFFAFACAC),
    outline = Color(0xFF727180),
    error = Color(0xFFFFB4AB),
    onError = Color.Black,
)

/**
 * Root Compose theme for Whizzz.
 *
 * @author udit
 */
@Composable
fun WhizzzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    useBrandDarkColors: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        useBrandDarkColors && darkTheme -> WhizzzBrandDarkScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = WhizzzPurple,
            secondary = WhizzzPurpleDark,
            tertiary = WhizzzPurple,
        )
        else -> lightColorScheme(
            primary = WhizzzPurple,
            secondary = WhizzzPurpleDark,
            tertiary = WhizzzPurple,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
