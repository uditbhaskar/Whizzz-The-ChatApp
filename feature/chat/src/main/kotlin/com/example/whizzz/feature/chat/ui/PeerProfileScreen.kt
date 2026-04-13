package com.example.whizzz.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whizzz.core.ui.theme.WhizzzTheme
import com.example.whizzz.domain.model.User
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.feature.chat.presentation.PeerProfileUiEvent
import com.example.whizzz.feature.chat.presentation.PeerProfileUiState
import com.example.whizzz.feature.chat.presentation.PeerProfileViewModel
import com.example.whizzz.core.ui.AnimatedProfilePhotoViewerOverlay
import com.example.whizzz.core.ui.WhizzzProfileAvatar
import com.example.whizzz.core.ui.WhizzzProfileDetailRow
import com.example.whizzz.core.ui.WhizzzProfileGroupedDivider
import com.example.whizzz.core.ui.WhizzzProfileGroupedList
import com.example.whizzz.core.ui.WhizzzProfileMuted
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.domain.text.DisplayTextLimits

/**
 * Stateless peer profile UI: grouped fields and fullscreen photo viewer.
 *
 * @param ui MVI [PeerProfileUiState].
 * @param onBack Navigate up.
 * @param onRetry Retry loading the profile ([PeerProfileUiEvent.Retry]).
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PeerProfileScreenContent(
    ui: PeerProfileUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val user = ui.user
    val loadError = ui.loadError
    var showPhotoViewer by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhizzzScreenBackground),
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = WhizzzStrings.Ui.PROFILE,
                        color = Color.White,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
            when {
                user != null -> {
                    val u = user!!
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(168.dp)
                                .clip(CircleShape)
                                .clickable { showPhotoViewer = true },
                        ) {
                            WhizzzProfileAvatar(
                                imageUrl = u.imageUrl,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = u.username,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                        )
                        Text(
                            text = WhizzzStrings.Ui.PROFILE_PHOTO_VIEW_HINT,
                            color = WhizzzProfileMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        WhizzzProfileGroupedList(Modifier.padding(horizontal = 16.dp)) {
                            WhizzzProfileDetailRow(
                                label = WhizzzStrings.Ui.NAME,
                                value = u.username,
                                valueMaxLines = 2,
                            )
                            WhizzzProfileGroupedDivider()
                            WhizzzProfileDetailRow(
                                label = WhizzzStrings.Ui.ABOUT,
                                value = u.bio.ifBlank { WhizzzStrings.Defaults.NEW_USER_BIO },
                                valueMaxLines = DisplayTextLimits.MAX_BIO_LINES,
                            )
                        }
                        Spacer(Modifier.height(32.dp))
                    }
                }
                loadError != null -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = loadError!!,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(onClick = onRetry) {
                            Text(WhizzzStrings.Ui.TRY_AGAIN)
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
        AnimatedProfilePhotoViewerOverlay(
            visible = showPhotoViewer && user != null,
            imageUrl = user?.imageUrl.orEmpty(),
            title = user?.username.orEmpty(),
            onDismiss = { showPhotoViewer = false },
        )
    }
}

/**
 * Peer profile route: [PeerProfileViewModel] and [PeerProfileScreenContent].
 *
 * @param onBack Navigate up.
 * @param viewModel MVI [PeerProfileViewModel].
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerProfileRoute(
    onBack: () -> Unit,
    viewModel: PeerProfileViewModel = koinViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    PeerProfileScreenContent(
        ui = ui,
        onBack = onBack,
        onRetry = { viewModel.onEvent(PeerProfileUiEvent.Retry) },
    )
}

/**
 * Compose preview for [PeerProfileScreenContent] with a loaded sample [User].
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun PeerProfileScreenPreview() {
    val sample = User(
        id = "1",
        username = "Alex",
        emailId = "",
        timestamp = "0",
        imageUrl = "",
        bio = "Building Whizzz · coffee · photos",
        status = "",
        searchKey = "alex",
    )
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        PeerProfileScreenContent(
            ui = PeerProfileUiState(user = sample, loadError = null),
            onBack = {},
            onRetry = {},
        )
    }
}

/**
 * Compose preview for [PeerProfileScreenContent] in the error + retry state.
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Peer profile · error", showSystemUi = false)
@Composable
private fun PeerProfileErrorPreview() {
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        PeerProfileScreenContent(
            ui = PeerProfileUiState(user = null, loadError = "Could not load profile."),
            onBack = {},
            onRetry = {},
        )
    }
}
