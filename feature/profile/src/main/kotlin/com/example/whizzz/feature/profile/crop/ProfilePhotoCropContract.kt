package com.example.whizzz.feature.profile.crop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.IntentCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

/**
 * Input for [ProfilePhotoCropContract] (URI plus canhub crop options).
 *
 *
 * @property uri Image to crop, or null to use the crop activity’s own source picker.
 * @property cropImageOptions Cropping UI and output options for [WhizzzProfileCropActivity].
 * @author udit
 */
data class ProfilePhotoCropInput(
    val uri: Uri?,
    val cropImageOptions: CropImageOptions,
)

/**
 * Starts [WhizzzProfileCropActivity] and parses its result (same intent extras as can hub's crop flow).
 * @author udit
 */
class ProfilePhotoCropContract : ActivityResultContract<ProfilePhotoCropInput, CropImageView.CropResult>() {

    override fun createIntent(context: Context, input: ProfilePhotoCropInput): Intent =
        Intent(context, WhizzzProfileCropActivity::class.java).apply {
            putExtra(
                CropImage.CROP_IMAGE_EXTRA_BUNDLE,
                Bundle(2).apply {
                    putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, input.uri)
                    putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, input.cropImageOptions)
                },
            )
        }

    override fun parseResult(resultCode: Int, intent: Intent?): CropImageView.CropResult {
        val result = intent?.let {
            IntentCompat.getParcelableExtra(
                it,
                CropImage.CROP_IMAGE_EXTRA_RESULT,
                CropImage.ActivityResult::class.java,
            )
        }
        return if (result == null || resultCode == Activity.RESULT_CANCELED) {
            CropImage.CancelledResult
        } else {
            result
        }
    }
}
