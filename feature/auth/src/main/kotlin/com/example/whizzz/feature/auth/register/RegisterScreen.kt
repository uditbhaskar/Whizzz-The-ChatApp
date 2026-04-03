package com.example.whizzz.feature.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.text.DisplayTextLimits
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.feature.auth.R
import com.example.whizzz.feature.auth.ui.AuthAccent
import com.example.whizzz.feature.auth.ui.AuthHint
import com.example.whizzz.feature.auth.ui.AuthBlack
import com.example.whizzz.feature.auth.ui.AuthLottieHeader
import com.example.whizzz.feature.auth.ui.AuthLoadingOverlay
import com.example.whizzz.feature.auth.ui.AuthUnderlinedField
import com.example.whizzz.feature.auth.ui.AuthCursiveFamily
import com.example.whizzz.feature.auth.ui.FramedAuthButton
import kotlinx.coroutines.flow.collectLatest

/**
 * Registration screen (Compose).
 *
 * @author udit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterRoute(
    onSuccess: () -> Unit,
    onLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                RegisterUiEffect.NavigateHome -> {
                    viewModel.onEvent(RegisterUiEvent.ClearForm)
                    onSuccess()
                }
            }
        }
    }

    Box(Modifier.fillMaxSize().background(AuthBlack)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .whizzzKeyboardInsetPadding()
                .verticalScroll(rememberScrollState())
                .imeNestedScroll()
                .padding(horizontal = 20.dp),
        ) {
            AuthLottieHeader(R.raw.signin_page_animation)
            Text(
                text = WhizzzStrings.Ui.WELCOME_SIGN,
                color = Color.White,
                fontFamily = AuthCursiveFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 25.dp, vertical = 4.dp),
            )
            Text(
                text = WhizzzStrings.Ui.SIGN_UP_HERE,
                color = Color.White,
                fontFamily = AuthCursiveFamily,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 27.dp, vertical = 2.dp),
            )
            Spacer(Modifier.height(24.dp))
            AuthUnderlinedField(
                value = state.username,
                onValueChange = { viewModel.onEvent(RegisterUiEvent.UsernameChanged(it)) },
                placeholder = WhizzzStrings.Ui.USERNAME,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
            )
            Text(
                text = "Up to ${DisplayTextLimits.MAX_USERNAME_CHARS} characters · one line",
                color = AuthHint,
                fontFamily = AuthCursiveFamily,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp, top = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
            AuthUnderlinedField(
                value = state.email,
                onValueChange = { viewModel.onEvent(RegisterUiEvent.EmailChanged(it)) },
                placeholder = WhizzzStrings.Ui.EMAIL,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
            )
            Spacer(Modifier.height(8.dp))
            AuthUnderlinedField(
                value = state.password,
                onValueChange = { viewModel.onEvent(RegisterUiEvent.PasswordChanged(it)) },
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
                            viewModel.onEvent(RegisterUiEvent.Submit)
                        }
                    },
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) {
                                WhizzzStrings.Ui.HIDE_PASSWORD
                            } else {
                                WhizzzStrings.Ui.SHOW_PASSWORD
                            },
                            tint = Color.White,
                        )
                    }
                },
            )
            state.errorMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    color = AuthAccent,
                    fontSize = 13.sp,
                    fontFamily = AuthCursiveFamily,
                    modifier = Modifier.padding(horizontal = 28.dp),
                )
            }
            Spacer(Modifier.height(36.dp))
            FramedAuthButton(
                text = WhizzzStrings.Ui.SIGN_UP,
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    viewModel.onEvent(RegisterUiEvent.Submit)
                },
                enabled = !state.loading,
            )
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = WhizzzStrings.Ui.ALREADY_HAVE_AN_ACCOUNT,
                    color = Color.White,
                    fontFamily = AuthCursiveFamily,
                    fontSize = 19.sp,
                )
                Text(
                    text = WhizzzStrings.Ui.LOGIN_HERE,
                    color = AuthAccent,
                    fontFamily = AuthCursiveFamily,
                    fontSize = 19.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .clickable(onClick = onLogin),
                )
            }
            Spacer(Modifier.height(32.dp))
        }
        AuthLoadingOverlay(state.loading)
    }
}
