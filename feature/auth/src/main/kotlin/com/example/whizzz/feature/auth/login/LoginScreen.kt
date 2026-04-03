package com.example.whizzz.feature.auth.login

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
import androidx.compose.material3.HorizontalDivider
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
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.feature.auth.ui.AuthAccent
import com.example.whizzz.feature.auth.ui.AuthBlack
import com.example.whizzz.feature.auth.ui.AuthLine
import com.example.whizzz.feature.auth.ui.AuthLottieHeader
import com.example.whizzz.feature.auth.ui.AuthLoadingOverlay
import com.example.whizzz.feature.auth.ui.AuthUnderlinedField
import com.example.whizzz.feature.auth.ui.AuthCursiveFamily
import com.example.whizzz.feature.auth.ui.FramedAuthButton
import com.example.whizzz.feature.auth.R
import kotlinx.coroutines.flow.collectLatest

/**
 * Login screen (Compose).
 *
 * @author udit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginRoute(
    onSuccess: () -> Unit,
    onRegister: () -> Unit,
    onForgot: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
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

    Box(Modifier.fillMaxSize().background(AuthBlack)) {
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
                color = Color.White,
                fontFamily = AuthCursiveFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 25.dp, vertical = 4.dp),
            )
            Text(
                text = WhizzzStrings.Ui.LOG_IN_TO_CONTINUE,
                color = Color.White,
                fontFamily = AuthCursiveFamily,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 27.dp, vertical = 2.dp),
            )
            Spacer(Modifier.height(24.dp))
            AuthUnderlinedField(
                value = state.email,
                onValueChange = { viewModel.onEvent(LoginUiEvent.EmailChanged(it)) },
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
                onValueChange = { viewModel.onEvent(LoginUiEvent.PasswordChanged(it)) },
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
                            viewModel.onEvent(LoginUiEvent.Submit)
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
                text = WhizzzStrings.Ui.LOGIN,
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    viewModel.onEvent(LoginUiEvent.Submit)
                },
                enabled = !state.loading,
            )
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = WhizzzStrings.Ui.DONT_HAVE_AN_ACCOUNT,
                    color = Color.White,
                    fontFamily = AuthCursiveFamily,
                    fontSize = 19.sp,
                )
                Text(
                    text = WhizzzStrings.Ui.REGISTER,
                    color = AuthAccent,
                    fontFamily = AuthCursiveFamily,
                    fontSize = 19.sp,
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .clickable(onClick = onRegister),
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp),
                thickness = 0.3.dp,
                color = AuthLine,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable(onClick = onForgot),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = WhizzzStrings.Ui.FORGET_PASSWORD_LINE,
                    color = AuthAccent,
                    fontFamily = AuthCursiveFamily,
                    fontSize = 16.5.sp,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp),
                thickness = 0.3.dp,
                color = AuthLine,
            )
            Spacer(Modifier.height(48.dp))
        }
        AuthLoadingOverlay(state.loading)
    }
}
