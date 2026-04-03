package com.example.whizzz.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.domain.text.DisplayTextLimits
import com.example.whizzz.core.ui.R as UiR
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.core.ui.WhizzzProfileAvatar
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val LabelMuted = Color(0xFFAFACAC)
private val BioSurface = Color(0xBADDD7D7)
private val BioText = Color.Black

/**
 * Profile tab matching original layout: header, avatar + camera, name/bio blocks, footer art.
 *
 * @author udit
 */
@Composable
fun ProfileRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val raw = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: return@launch
                val bytes = jpegBytesForProfileUpload(raw)
                viewModel.onEvent(ProfileUiEvent.PhotoPicked(bytes))
            }
        }
    }

    when (state.dialog) {
        ProfileDialog.Username -> EditDialog(
            title = WhizzzStrings.Ui.EDIT_USERNAME,
            text = state.dialogText,
            errorText = state.error,
            onTextChange = { viewModel.onEvent(ProfileUiEvent.DialogTextChanged(it)) },
            onDismiss = { viewModel.onEvent(ProfileUiEvent.DismissDialog) },
            onSave = { viewModel.onEvent(ProfileUiEvent.SaveDialog) },
            loading = state.loading,
        )
        ProfileDialog.Bio -> EditDialog(
            title = WhizzzStrings.Ui.EDIT_BIO,
            text = state.dialogText,
            errorText = state.error,
            onTextChange = { viewModel.onEvent(ProfileUiEvent.DialogTextChanged(it)) },
            onDismiss = { viewModel.onEvent(ProfileUiEvent.DismissDialog) },
            onSave = { viewModel.onEvent(ProfileUiEvent.SaveDialog) },
            loading = state.loading,
        )
        ProfileDialog.None -> Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhizzzScreenBackground),
    ) {
        val user = state.user
        if (user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (val loadErr = state.profileLoadError) {
                    null -> CircularProgressIndicator(color = Color.White)
                    else -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp),
                    ) {
                        Text(
                            text = loadErr,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = { viewModel.onEvent(ProfileUiEvent.RetryLoadProfile) },
                        ) {
                            Text(WhizzzStrings.Ui.TRY_AGAIN)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { pickImage.launch("image/*") },
                    ) {
                        WhizzzProfileAvatar(
                            imageUrl = user.imageUrl,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(2.dp)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .clickable { pickImage.launch("image/*") },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = WhizzzStrings.Ui.CHANGE_PHOTO,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(2.dp),
                            )
                        }
                    }
                }
                if (state.photoUploading) {
                    CircularProgressIndicator(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp),
                        color = Color.White,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = WhizzzStrings.Ui.NAME,
                            color = LabelMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(2.dp),
                        )
                        Text(
                            text = user.username,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp),
                        )
                    }
                    IconButton(
                        onClick = { viewModel.onEvent(ProfileUiEvent.OpenUsernameDialog) },
                        modifier = Modifier.padding(end = 10.dp),
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = WhizzzStrings.Ui.EDIT_USERNAME, tint = Color.White)
                    }
                }
                Text(
                    text = WhizzzStrings.Ui.BIO,
                    color = LabelMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 22.dp, bottom = 3.dp, end = 2.dp),
                )
                Text(
                    text = user.bio.ifBlank { WhizzzStrings.Defaults.NEW_USER_BIO },
                    color = BioText,
                    fontSize = 15.sp,
                    maxLines = DisplayTextLimits.MAX_BIO_LINES,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 10.dp, bottom = 8.dp)
                        .background(BioSurface, RoundedCornerShape(30.dp))
                        .clickable { viewModel.onEvent(ProfileUiEvent.OpenBioDialog) }
                        .padding(20.dp),
                )
                state.error?.let { err ->
                    Text(
                        err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
            Image(
                painter = painterResource(UiR.drawable.ic_whizzz_profile_footer),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp)
                    .padding(horizontal = 10.dp, vertical = 20.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun EditDialog(
    title: String,
    text: String,
    errorText: String?,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    loading: Boolean,
) {
    val dialogScroll = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.whizzzKeyboardInsetPadding(),
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(dialogScroll),
            ) {
                val isBio = title == WhizzzStrings.Ui.EDIT_BIO
                val isUsername = title == WhizzzStrings.Ui.EDIT_USERNAME
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = !isBio,
                    minLines = if (isBio) 3 else 1,
                    maxLines = if (isBio) DisplayTextLimits.MAX_BIO_LINES else 1,
                    isError = errorText != null,
                    supportingText = when {
                        isBio -> {
                            {
                                Text(
                                    text = "Maximum ${DisplayTextLimits.MAX_BIO_LINES} lines",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        isUsername -> {
                            {
                                Text(
                                    text = "Up to ${DisplayTextLimits.MAX_USERNAME_CHARS} characters · one line",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        else -> null
                    },
                )
                errorText?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = !loading) {
                Text(WhizzzStrings.Ui.SAVE)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(WhizzzStrings.Ui.BACK)
            }
        },
    )
}
