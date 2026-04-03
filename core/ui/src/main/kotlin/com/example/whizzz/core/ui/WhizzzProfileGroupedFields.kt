package com.example.whizzz.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProfileGroupedSurface = Color(0xFF1F2C34)
private val ProfileGroupedDividerColor = Color(0xFF2A3942)

val WhizzzProfileMuted = Color(0xFF8696A0)
private val ProfileGroupedValueColor = Color(0xFFE9EDEF)

/**
 * Rounded grouped list surface for profile name, about, and similar rows.
 *
 * @param modifier Optional [Modifier].
 * @param content Rows and [WhizzzProfileGroupedDivider] calls.
 * @author udit
 */
@Composable
fun WhizzzProfileGroupedList(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ProfileGroupedSurface),
        content = content,
    )
}

/**
 * One profile field row: muted label, primary value, optional trailing chevron when tappable.
 *
 * @param label Row title (e.g. Name, About).
 * @param value Shown body text.
 * @param modifier Optional [Modifier].
 * @param onClick When non-null, the row is clickable.
 * @param valueMaxLines Line cap for the value.
 * @param showChevron Trailing chevron (typically when [onClick] is set).
 * @author udit
 */
@Composable
fun WhizzzProfileDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    valueMaxLines: Int = Int.MAX_VALUE,
    showChevron: Boolean = false,
) {
    val rowModifier =
        if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        }
    Column(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = label,
            color = WhizzzProfileMuted,
            fontSize = 14.sp,
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                color = ProfileGroupedValueColor,
                fontSize = 17.sp,
                maxLines = valueMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = WhizzzProfileMuted,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                )
            }
        }
    }
}

/**
 * Inset divider between rows inside [WhizzzProfileGroupedList].
 * @author udit
 */
@Composable
fun WhizzzProfileGroupedDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        thickness = 0.5.dp,
        color = ProfileGroupedDividerColor,
    )
}
