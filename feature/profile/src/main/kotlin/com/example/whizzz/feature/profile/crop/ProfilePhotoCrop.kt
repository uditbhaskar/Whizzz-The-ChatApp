package com.example.whizzz.feature.profile.crop

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

/**
 * Maximum width/height requested from the cropper before [jpegBytesForProfileUpload] runs (matches upload pipeline cap).
 * @author udit
 */
private const val CROP_OUTPUT_MAX_EDGE = 1600

/**
 * Builds [ProfilePhotoCropInput] for square 1:1 profile photos.
 *
 * When [sourceUri] is non-null (gallery pick or camera capture), the crop screen opens on that image only and the
 * built-in source chooser is disabled so the app can use a custom bottom sheet instead.
 *
 *
 * @param sourceUri Image to crop, or null to let the cropper offer gallery/camera (legacy).
 * @return Input passed into [ProfilePhotoCropContract].
 * @author udit
 */
internal fun profilePhotoCropOptions(sourceUri: Uri? = null): ProfilePhotoCropInput {
    val opts = CropImageOptions()
    val hasSource = sourceUri != null
    opts.imageSourceIncludeGallery = !hasSource
    opts.imageSourceIncludeCamera = !hasSource
    opts.fixAspectRatio = true
    opts.aspectRatioX = 1
    opts.aspectRatioY = 1
    opts.guidelines = CropImageView.Guidelines.ON
    opts.outputCompressFormat = Bitmap.CompressFormat.JPEG
    opts.outputCompressQuality = 95
    opts.outputRequestWidth = CROP_OUTPUT_MAX_EDGE
    opts.outputRequestHeight = CROP_OUTPUT_MAX_EDGE
    opts.outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_INSIDE
    opts.activityBackgroundColor = Color.BLACK
    opts.toolbarColor = Color.BLACK
    opts.toolbarTitleColor = Color.WHITE
    opts.toolbarBackButtonColor = Color.WHITE
    opts.activityMenuIconColor = Color.WHITE
    return ProfilePhotoCropInput(sourceUri, opts)
}
