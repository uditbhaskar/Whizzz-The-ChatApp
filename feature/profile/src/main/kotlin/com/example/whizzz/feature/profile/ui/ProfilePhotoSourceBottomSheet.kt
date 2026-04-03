package com.example.whizzz.feature.profile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.theme.WhizzzTheme
import com.example.whizzz.core.ui.theme.WhizzzAccent
import com.example.whizzz.core.ui.theme.WhizzzSurfaceMuted

private val SheetDivider = Color.White.copy(alpha = 0.08f)

/**
 * Dark themed action sheet: choose gallery, take photo, or cancel (matches profile shell colors).
 *
 * @param visible When true, the sheet is shown.
 * @param onDismiss Invoked when the user scrims away or cancels.
 * @param onChooseGallery Opens the system photo picker.
 * @param onTakePhoto Opens the system camera into the prepared capture [android.net.Uri].
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfilePhotoSourceBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onChooseGallery: () -> Unit,
    onTakePhoto: () -> Unit,
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WhizzzSurfaceMuted,
        contentColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.35f))
        },
    ) {
        Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text(
                text = WhizzzStrings.Ui.PROFILE_PHOTO,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = SheetDivider)
            SheetRow(
                icon = { Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, tint = WhizzzAccent) },
                label = WhizzzStrings.Ui.CHOOSE_FROM_GALLERY,
                onClick = {
                    onChooseGallery()
                    onDismiss()
                },
            )
            HorizontalDivider(color = SheetDivider, modifier = Modifier.padding(start = 56.dp))
            SheetRow(
                icon = { Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = WhizzzAccent) },
                label = WhizzzStrings.Ui.TAKE_PHOTO,
                onClick = {
                    onTakePhoto()
                    onDismiss()
                },
            )
            HorizontalDivider(color = SheetDivider)
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            ) {
                Text(WhizzzStrings.Ui.CANCEL, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

/**
 * Single tappable row with leading icon and label (full-width hit target).
 *
 * @param icon Leading slot (typically a tinted [Icon]).
 * @param label Row title.
 * @param onClick Invoked when the row is pressed.
 * @author udit
 */
@Composable
private fun SheetRow(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoxWithMinIcon(icon)
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
    }
}

/**
 * Fixed-size box so leading icons in [SheetRow] share a consistent minimum width.
 *
 * @param icon Composable icon slot (typically 24–28 dp).
 * @author udit
 */
@Composable
private fun BoxWithMinIcon(icon: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
        icon()
    }
}

/**
 * Compose preview for [ProfilePhotoSourceBottomSheet] in the visible state.
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false, name = "Profile · photo source sheet")
@Composable
private fun ProfilePhotoSourceBottomSheetPreview() {
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        Box(Modifier.fillMaxWidth()) {
            ProfilePhotoSourceBottomSheet(
                visible = true,
                onDismiss = {},
                onChooseGallery = {},
                onTakePhoto = {},
            )
        }
    }
}
