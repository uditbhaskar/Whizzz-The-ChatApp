package com.example.whizzz.feature.auth.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.feature.auth.R
import com.example.whizzz.feature.auth.ui.AuthAccent
import com.example.whizzz.feature.auth.ui.AuthBlack
import com.example.whizzz.feature.auth.ui.AuthCursiveFamily
import com.example.whizzz.feature.auth.ui.AuthHint
import com.example.whizzz.feature.auth.ui.AuthSheetBackground
import com.example.whizzz.feature.auth.ui.AuthUnderlinedField
import com.example.whizzz.feature.auth.ui.FramedAuthButton

/**
 * Forgot-password screen (Compose).
 *
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ForgotRoute(
    onBack: () -> Unit,
    viewModel: ForgotViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = AuthSheetBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        WhizzzStrings.Ui.RESET_PASSWORD_TITLE,
                        color = Color.White,
                        fontFamily = AuthCursiveFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = WhizzzStrings.Ui.BACK,
                            tint = Color.White,
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AuthBlack,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .whizzzKeyboardInsetPadding()
                .background(AuthSheetBackground)
                .verticalScroll(rememberScrollState())
                .imeNestedScroll(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_password),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = WhizzzStrings.Ui.RESET_EMAIL_HINT,
                color = AuthHint,
                fontFamily = AuthCursiveFamily,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            Spacer(Modifier.height(16.dp))
            AuthUnderlinedField(
                value = state.email,
                onValueChange = { viewModel.onEvent(ForgotUiEvent.EmailChanged(it)) },
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
                            viewModel.onEvent(ForgotUiEvent.Submit)
                        }
                    },
                ),
            )
            state.errorMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    color = Color(0xFFFF8A80),
                    fontSize = 14.sp,
                    fontFamily = AuthCursiveFamily,
                    modifier = Modifier.padding(horizontal = 28.dp),
                )
            }
            state.message?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    color = AuthAccent,
                    fontSize = 14.sp,
                    fontFamily = AuthCursiveFamily,
                    modifier = Modifier.padding(horizontal = 28.dp),
                )
            }
            Spacer(Modifier.height(28.dp))
            FramedAuthButton(
                text = WhizzzStrings.Ui.SEND_RESET,
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    viewModel.onEvent(ForgotUiEvent.Submit)
                },
                enabled = !state.loading,
            )
            Spacer(Modifier.height(16.dp))
            if (state.loading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
