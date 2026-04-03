package com.example.whizzz.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.media.isExportableProfileImageUrl
import com.example.whizzz.core.ui.media.profileImageToShareJpegFile
import com.example.whizzz.core.ui.media.saveJpegToGallery
import com.example.whizzz.core.ui.media.shareProfileImageFile
import kotlinx.coroutines.launch

/**
 * Full-screen profile photo with share and save actions.
 *
 * @param imageUrl Source passed through to [WhizzzProfileAvatar] and export helpers.
 * @param title Top-bar label for context (e.g. peer name).
 * @param onDismiss Close action from toolbar back affordance.
 * @param onEditPhoto When non-null, shows an edit control to change the picture (e.g. own profile).
 * @author udit
 */
@Composable
fun ProfilePhotoViewerOverlay(
    imageUrl: String,
    title: String,
    onDismiss: () -> Unit,
    onEditPhoto: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val exportable = isExportableProfileImageUrl(imageUrl)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = WhizzzStrings.Ui.CLOSE,
                        tint = Color.White,
                    )
                }
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                )
                if (onEditPhoto != null) {
                    IconButton(onClick = onEditPhoto) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = WhizzzStrings.Ui.CHANGE_PHOTO,
                            tint = Color.White,
                        )
                    }
                }
                IconButton(
                    onClick = {
                        if (!exportable) {
                            scope.launch {
                                snackBarHostState.showSnackbar(WhizzzStrings.Ui.NO_PHOTO_TO_SHARE)
                            }
                            return@IconButton
                        }
                        scope.launch {
                            val file = context.profileImageToShareJpegFile(imageUrl)
                            if (file != null) {
                                context.shareProfileImageFile(file)
                            } else {
                                snackBarHostState.showSnackbar(WhizzzStrings.Ui.NO_PHOTO_TO_SHARE)
                            }
                        }
                    },
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = WhizzzStrings.Ui.SHARE,
                        tint = Color.White,
                    )
                }
                IconButton(
                    onClick = {
                        if (!exportable) {
                            scope.launch {
                                snackBarHostState.showSnackbar(WhizzzStrings.Ui.NO_PHOTO_TO_SHARE)
                            }
                            return@IconButton
                        }
                        scope.launch {
                            val file = context.profileImageToShareJpegFile(imageUrl)
                            if (file == null) {
                                snackBarHostState.showSnackbar(WhizzzStrings.Ui.COULD_NOT_SAVE_PHOTO)
                                return@launch
                            }
                            val bytes = file.readBytes()
                            val ok = context.saveJpegToGallery(bytes)
                            snackBarHostState.showSnackbar(
                                if (ok) {
                                    WhizzzStrings.Ui.PHOTO_SAVED
                                } else {
                                    WhizzzStrings.Ui.COULD_NOT_SAVE_PHOTO
                                },
                            )
                        }
                    },
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = WhizzzStrings.Ui.SAVE_TO_GALLERY,
                        tint = Color.White,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                WhizzzProfileAvatar(
                    imageUrl = imageUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
