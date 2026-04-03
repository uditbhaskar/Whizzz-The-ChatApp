package com.example.whizzz.feature.auth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.whizzz.core.ui.WhizzzPacificoFamily
import com.example.whizzz.core.ui.theme.WhizzzAccent
import com.example.whizzz.core.ui.theme.WhizzzAccentSecondary
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.core.ui.theme.WhizzzSurfaceMuted
import com.example.whizzz.core.ui.theme.WhizzzTheme
import com.example.whizzz.feature.auth.R

internal val AuthBarBlack = Color.Black

private val AuthFieldShape = RoundedCornerShape(12.dp)
private val AuthButtonShape = RoundedCornerShape(12.dp)

/**
 * Shared auth UI: Lottie hero, [AuthOutlinedField], primary [FramedAuthButton], loading overlay.
 * Styling matches in-app screens ([WhizzzScreenBackground], Material typography;
 * [WhizzzPacificoFamily] for preview placeholder only). Lottie has no enclosing panel.
 */
@Composable
internal fun AuthLottieHeader(rawRes: Int, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        AuthLottieHeaderPreviewPlaceholder(modifier)
        return
    }
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

@Composable
private fun AuthLottieHeaderPreviewPlaceholder(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(end = 50.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WhizzzAccent.copy(alpha = 0.35f),
                        WhizzzScreenBackground,
                        WhizzzAccentSecondary.copy(alpha = 0.25f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Whizzz",
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 32.sp,
            fontFamily = WhizzzPacificoFamily,
        )
    }
}

@Composable
internal fun AuthOutlinedField(
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
    val scheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = scheme.onSurfaceVariant,
            )
        },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = AuthFieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = scheme.onSurface,
            unfocusedTextColor = scheme.onSurface,
            focusedContainerColor = WhizzzSurfaceMuted,
            unfocusedContainerColor = WhizzzSurfaceMuted,
            disabledContainerColor = WhizzzSurfaceMuted.copy(alpha = 0.5f),
            cursorColor = WhizzzAccent,
            focusedBorderColor = WhizzzAccent,
            unfocusedBorderColor = scheme.outline,
            disabledBorderColor = scheme.outline.copy(alpha = 0.5f),
        ),
    )
}

@Composable
internal fun FramedAuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val scheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 4.dp),
        shape = AuthButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = scheme.primary,
            contentColor = scheme.onPrimary,
            disabledContainerColor = scheme.primary.copy(alpha = 0.38f),
            disabledContentColor = scheme.onPrimary.copy(alpha = 0.6f),
        ),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
internal fun AuthLoadingOverlay(visible: Boolean) {
    if (!visible) return
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = WhizzzAccent)
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF16191C,
    showSystemUi = false,
    name = "Auth · field + button",
)
@Composable
private fun AuthComponentsPreview() {
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        Column(
            Modifier
                .fillMaxSize()
                .background(WhizzzScreenBackground)
                .padding(16.dp),
        ) {
            AuthLottieHeader(R.raw.login_back_ground)
            Spacer(Modifier.height(16.dp))
            AuthOutlinedField(
                value = "preview@example.com",
                onValueChange = {},
                placeholder = "Email",
            )
            Spacer(Modifier.height(16.dp))
            FramedAuthButton(text = "Continue", onClick = {})
        }
    }
}
