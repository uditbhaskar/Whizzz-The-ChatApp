package com.example.whizzz.core.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.whizzz.core.strings.WhizzzStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Decodes a `data:image/...;base64,...` payload to an [ImageBitmap], or null if invalid.
 *
 * @param dataUri Full data URI including base64 segment.
 * @return Compose bitmap or `null` when decoding fails.
 * @author udit
 */
internal fun decodeDataUriToImageBitmap(dataUri: String): ImageBitmap? {
    if (!dataUri.startsWith("data:image/", ignoreCase = true)) return null
    val comma = dataUri.indexOf(',')
    if (comma < 0) return null
    val header = dataUri.substring(0, comma)
    if (!header.contains("base64", ignoreCase = true)) return null
    val b64 = dataUri.substring(comma + 1).trim()
    return try {
        val bytes = Base64.decode(b64, Base64.DEFAULT)
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        bmp.asImageBitmap()
    } catch (_: Throwable) {
        null
    }
}

/**
 * Circular-style avatar: placeholder for default profile, in-memory bitmap for data URIs, or [AsyncImage] for HTTP(S).
 *
 * @param imageUrl Raw URL, `"default"`, blank, or a base64 data URI from the backend.
 * @param modifier Layout and graphics modifier for the underlying image.
 * @param contentScale How the bitmap or remote image fits its bounds.
 * @author udit
 */
@Composable
fun WhizzzProfileAvatar(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    val placeholder = painterResource(R.drawable.ic_default_profile_avatar)
    val isDefault = imageUrl.isBlank() || imageUrl == WhizzzStrings.Defaults.PROFILE_IMAGE
    val isDataUri = imageUrl.startsWith("data:image/", ignoreCase = true)

    var dataBitmap by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        dataBitmap = if (isDataUri) {
            withContext(Dispatchers.Default) {
                decodeDataUriToImageBitmap(imageUrl)
            }
        } else {
            null
        }
    }

    when {
        isDefault -> Image(placeholder, null, modifier, contentScale = contentScale)
        isDataUri -> {
            val bmp = dataBitmap
            if (bmp != null) {
                Image(bmp, null, modifier, contentScale = contentScale)
            } else {
                Image(placeholder, null, modifier, contentScale = contentScale)
            }
        }
        else -> {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = modifier,
                contentScale = contentScale,
                placeholder = placeholder,
                error = placeholder,
            )
        }
    }
}
