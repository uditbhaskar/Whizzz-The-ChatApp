package com.example.whizzz.feature.auth.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whizzz.core.ui.WhizzzPacificoFamily
import com.example.whizzz.core.ui.theme.WhizzzAccent
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.core.ui.theme.WhizzzTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.feature.auth.ui.components.AuthLoadingOverlay
import com.example.whizzz.feature.auth.ui.components.AuthLottieHeader
import com.example.whizzz.feature.auth.ui.components.AuthOutlinedField
import com.example.whizzz.feature.auth.ui.components.FramedAuthButton
import com.example.whizzz.feature.auth.R
import com.example.whizzz.feature.auth.presentation.login.LoginUiEffect
import com.example.whizzz.feature.auth.presentation.login.LoginUiEvent
import com.example.whizzz.feature.auth.presentation.login.LoginUiState
import com.example.whizzz.feature.auth.presentation.login.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateless login UI: form, Lottie header, links to register and forgot password.
 *
 * @param state MVI [LoginUiState] from [LoginViewModel].
 * @param passwordVisible Whether the password field uses plain text or masking.
 * @param onPasswordVisibleChange Toggles password visibility.
 * @param onEvent Dispatches [LoginUiEvent] to the ViewModel.
 * @param onRegister Navigate to registration.
 * @param onForgot Navigate to password reset.
 * @author udit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LoginScreenContent(
    state: LoginUiState,
    passwordVisible: Boolean,
    onPasswordVisibleChange: () -> Unit,
    onEvent: (LoginUiEvent) -> Unit,
    onRegister: () -> Unit,
    onForgot: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Box(Modifier.fillMaxSize().background(WhizzzScreenBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .whizzzKeyboardInsetPadding()
                .verticalScroll(rememberScrollState())
                .imeNestedScroll()
                .padding(horizontal = 20.dp),
        ) {
            AuthLottieHeader(R.raw.login_back_ground)
            Text(
                text = WhizzzStrings.Ui.HI_THERE,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = WhizzzPacificoFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            )
            Text(
                text = WhizzzStrings.Ui.LOG_IN_TO_CONTINUE,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
            Spacer(Modifier.height(20.dp))
            AuthOutlinedField(
                value = state.email,
                onValueChange = { onEvent(LoginUiEvent.EmailChanged(it)) },
                placeholder = WhizzzStrings.Ui.EMAIL,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
            )
            Spacer(Modifier.height(12.dp))
            AuthOutlinedField(
                value = state.password,
                onValueChange = { onEvent(LoginUiEvent.PasswordChanged(it)) },
                placeholder = WhizzzStrings.Ui.PASSWORD,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!state.loading) {
                            focusManager.clearFocus()
                            keyboard?.hide()
                            onEvent(LoginUiEvent.Submit)
                        }
                    },
                ),
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibleChange) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) {
                                WhizzzStrings.Ui.HIDE_PASSWORD
                            } else {
                                WhizzzStrings.Ui.SHOW_PASSWORD
                            },
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
            state.errorMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
            Spacer(Modifier.height(28.dp))
            FramedAuthButton(
                text = WhizzzStrings.Ui.LOGIN,
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    onEvent(LoginUiEvent.Submit)
                },
                enabled = !state.loading,
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = WhizzzStrings.Ui.DONT_HAVE_AN_ACCOUNT,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = WhizzzStrings.Ui.REGISTER,
                    color = WhizzzAccent,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clickable(onClick = onRegister),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(onClick = onForgot),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = WhizzzStrings.Ui.FORGET_PASSWORD_LINE.trimEnd(),
                    color = WhizzzAccent,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(32.dp))
        }
        AuthLoadingOverlay(state.loading)
    }
}

/**
 * Login screen route: wires [LoginViewModel] (MVI), collects navigation [LoginUiEffect], hosts [LoginScreenContent].
 *
 * @param onSuccess Invoked after successful sign-in (e.g. navigate to home).
 * @param onRegister Navigate to registration.
 * @param onForgot Navigate to forgot password.
 * @param viewModel Injected [LoginViewModel].
 * @author udit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginRoute(
    onSuccess: () -> Unit,
    onRegister: () -> Unit,
    onForgot: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                LoginUiEffect.NavigateHome -> {
                    viewModel.onEvent(LoginUiEvent.ClearForm)
                    onSuccess()
                }
            }
        }
    }

    LoginScreenContent(
        state = state,
        passwordVisible = passwordVisible,
        onPasswordVisibleChange = { passwordVisible = !passwordVisible },
        onEvent = viewModel::onEvent,
        onRegister = onRegister,
        onForgot = onForgot,
    )
}

/**
 * Compose preview for [LoginScreenContent] with sample credentials and themed Lottie placeholder.
 * @author udit
 */
@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun LoginScreenPreview() {
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        LoginScreenContent(
            state = LoginUiState(
                email = "you@example.com",
                password = "••••••••",
                loading = false,
                errorMessage = null,
            ),
            passwordVisible = false,
            onPasswordVisibleChange = {},
            onEvent = {},
            onRegister = {},
            onForgot = {},
        )
    }
}
