package com.example.whizzz.feature.auth.ui.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whizzz.core.ui.theme.WhizzzAccent
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.core.ui.theme.WhizzzSurfaceMuted
import com.example.whizzz.core.ui.theme.WhizzzTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.feature.auth.R
import com.example.whizzz.feature.auth.presentation.forgot.ForgotUiEvent
import com.example.whizzz.feature.auth.presentation.forgot.ForgotUiState
import com.example.whizzz.feature.auth.presentation.forgot.ForgotViewModel
import com.example.whizzz.feature.auth.ui.components.AuthBarBlack
import com.example.whizzz.feature.auth.ui.components.AuthOutlinedField
import com.example.whizzz.feature.auth.ui.components.FramedAuthButton

/**
 * Forgot-password UI scaffold: top bar, illustration, email field, submit.
 *
 * @param state MVI [ForgotUiState].
 * @param onBack Navigate up.
 * @param onEvent Dispatches [ForgotUiEvent].
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun ForgotScreenContent(
    state: ForgotUiState,
    onBack: () -> Unit,
    onEvent: (ForgotUiEvent) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = WhizzzScreenBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        WhizzzStrings.Ui.RESET_PASSWORD_TITLE,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = WhizzzStrings.Ui.BACK,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AuthBarBlack,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .whizzzKeyboardInsetPadding()
                .background(WhizzzScreenBackground)
                .verticalScroll(rememberScrollState())
                .imeNestedScroll()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_password),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 24.dp)
                    .background(WhizzzSurfaceMuted, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = WhizzzStrings.Ui.RESET_EMAIL_HINT,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            Spacer(Modifier.height(16.dp))
            AuthOutlinedField(
                value = state.email,
                onValueChange = { onEvent(ForgotUiEvent.EmailChanged(it)) },
                placeholder = WhizzzStrings.Ui.EMAIL,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!state.loading) {
                            focusManager.clearFocus()
                            keyboard?.hide()
                            onEvent(ForgotUiEvent.Submit)
                        }
                    },
                ),
            )
            state.errorMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            state.message?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    color = WhizzzAccent,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            Spacer(Modifier.height(28.dp))
            FramedAuthButton(
                text = WhizzzStrings.Ui.SEND_RESET,
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    onEvent(ForgotUiEvent.Submit)
                },
                enabled = !state.loading,
            )
            Spacer(Modifier.height(16.dp))
            if (state.loading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = WhizzzAccent,
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

/**
 * Forgot-password route: [ForgotViewModel] and [ForgotScreenContent].
 *
 * @param onBack Navigate up.
 * @param viewModel Injected [ForgotViewModel].
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ForgotRoute(
    onBack: () -> Unit,
    viewModel: ForgotViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ForgotScreenContent(
        state = state,
        onBack = onBack,
        onEvent = viewModel::onEvent,
    )
}

/**
 * Compose preview for [ForgotScreenContent] (reset-password scaffold; no Lottie on this screen).
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun ForgotScreenPreview() {
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        ForgotScreenContent(
            state = ForgotUiState(
                email = "recover@example.com",
                loading = false,
                errorMessage = null,
                message = null,
            ),
            onBack = {},
            onEvent = {},
        )
    }
}
