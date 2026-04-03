/**
 * Shared Compose UI for authentication flows: colors, underlined [TextField], Lottie header, and framed buttons.
 *
 * @author udit
 */
package com.example.whizzz.feature.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.whizzz.feature.auth.R

internal val AuthBlack = Color.Black
internal val AuthHint = Color(0xFFA9A8BC)
internal val AuthMint = Color(0xFF63FFA3)
internal val AuthAccent = Color(0xFF63FF90)
internal val AuthLine = Color(0xFF727180)
internal val AuthSheetBackground = Color(0xFF16191C)

/** Cursive used across login, sign-up, and reset-password screens. */
internal val AuthCursiveFamily = FontFamily(Font(R.font.pacifico))

/** Looping Lottie animation used as the hero on auth screens. */
@Composable
internal fun AuthLottieHeader(rawRes: Int, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(end = 50.dp),
    )
}

/** Underlined single- or multi-line field matching the auth visual style. */
@Composable
internal fun AuthUnderlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(horizontal = 25.dp),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = AuthCursiveFamily,
        ),
        placeholder = {
            Text(
                placeholder,
                color = AuthHint,
                fontSize = 18.sp,
                fontFamily = AuthCursiveFamily,
            )
        },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = AuthMint,
            unfocusedIndicatorColor = AuthHint,
            disabledIndicatorColor = AuthHint,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
        ),
    )
}

/** Full-width outlined primary action (login, sign-up, send reset). */
@Composable
internal fun FramedAuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 25.dp),
        border = BorderStroke(4.dp, AuthLine),
        shape = RectangleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AuthBlack,
            contentColor = Color.White,
            disabledContainerColor = AuthBlack.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f),
        ),
    ) {
        Text(text, fontSize = 16.sp, fontFamily = AuthCursiveFamily)
    }
}

/** Dimmed full-screen scrim with spinner while auth requests run. */
@Composable
internal fun AuthLoadingOverlay(visible: Boolean) {
    if (!visible) return
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}
