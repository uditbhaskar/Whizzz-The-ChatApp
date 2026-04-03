package com.example.whizzz.feature.profile.crop

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.os.BundleCompat
import androidx.core.view.WindowCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.canhub.cropper.databinding.CropImageActivityBinding
import java.io.File

open class WhizzzProfileCropActivity :
  AppCompatActivity(),
  OnSetImageUriCompleteListener,
  OnCropImageCompleteListener {
  private var cropImageUri: Uri? = null
  private lateinit var cropImageOptions: CropImageOptions
  private var cropImageView: CropImageView? = null
  private lateinit var binding: CropImageActivityBinding
  private var latestTmpUri: Uri? = null
  private val pickImageGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    onPickImageResult(uri)
  }

  private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
    if (success) {
      onPickImageResult(latestTmpUri)
    } else {
      onPickImageResult(null)
    }
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    paintSystemChromeBlack()

    binding = CropImageActivityBinding.inflate(layoutInflater)
    setContentView(binding.root)
    findViewById<View>(android.R.id.content).setBackgroundColor(Color.BLACK)
    setCropImageView(binding.cropImageView)
    val bundle = intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
    cropImageUri = bundle?.let {
      BundleCompat.getParcelable(it, CropImage.CROP_IMAGE_EXTRA_SOURCE, Uri::class.java)
    }
    cropImageOptions =
      bundle?.let {
        BundleCompat.getParcelable(it, CropImage.CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions::class.java)
      } ?: CropImageOptions()

    if (savedInstanceState == null) {
      if (cropImageUri == null || cropImageUri == Uri.EMPTY) {
        when {
          cropImageOptions.showIntentChooser -> showImageSourceDialog(::openSource)
          cropImageOptions.imageSourceIncludeGallery &&
            cropImageOptions.imageSourceIncludeCamera ->
            showImageSourceDialog(::openSource)
          cropImageOptions.imageSourceIncludeGallery ->
            pickImageGallery.launch("image/*")
          cropImageOptions.imageSourceIncludeCamera ->
            openCamera()
          else -> finish()
        }
      } else {
        cropImageView?.setImageUriAsync(cropImageUri)
      }
    } else {
      latestTmpUri = savedInstanceState.getString(BUNDLE_KEY_TMP_URI)?.toUri()
    }

    setCustomizations()

    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          setResultCancel()
        }
      },
    )
  }

  private fun setCustomizations() {
    cropImageOptions.activityBackgroundColor.let { activityBackgroundColor ->
      binding.root.setBackgroundColor(activityBackgroundColor)
      findViewById<View>(android.R.id.content).setBackgroundColor(activityBackgroundColor)
    }

    supportActionBar?.let { actionBar ->
      title = cropImageOptions.activityTitle.ifEmpty { "" }
      actionBar.setDisplayHomeAsUpEnabled(true)
      cropImageOptions.toolbarColor?.let { toolbarColor ->
        actionBar.setBackgroundDrawable(toolbarColor.toDrawable())
      }
      cropImageOptions.toolbarTitleColor?.let { toolbarTitleColor ->
        val spannableTitle: Spannable = SpannableString(title)
        spannableTitle.setSpan(
          ForegroundColorSpan(toolbarTitleColor),
          0,
          spannableTitle.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
        title = spannableTitle
      }
      run {
        val backBtnColor = cropImageOptions.toolbarBackButtonColor ?: Color.WHITE
        try {
          val backDrawableId = mergedLibraryResId("ic_arrow_back_24", "drawable")
          val upArrow = if (backDrawableId != 0) {
            ContextCompat.getDrawable(this, backDrawableId)?.mutate()
          } else {
            null
          }
          upArrow?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            backBtnColor,
            BlendModeCompat.SRC_ATOP,
          )
          actionBar.setHomeAsUpIndicator(upArrow)
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
    applyStatusBarAndWindowBackground()
  }

  /**
   * Before [setContentView], align window/decor with black chrome. System bars are transparent
   * ([enableEdgeToEdge]); the background behind them must match.
   * @author udit
   */
  private fun paintSystemChromeBlack() {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.setBackgroundDrawable(Color.BLACK.toDrawable())
    window.decorView.setBackgroundColor(Color.BLACK)
    WindowCompat.getInsetsController(window, window.decorView).apply {
      isAppearanceLightStatusBars = false
      isAppearanceLightNavigationBars = false
    }
  }

  private fun applyStatusBarAndWindowBackground() {
    window.setBackgroundDrawable(
      cropImageOptions.activityBackgroundColor.toDrawable(),
    )
    window.decorView.setBackgroundColor(cropImageOptions.activityBackgroundColor)
    WindowCompat.getInsetsController(window, window.decorView).apply {
      isAppearanceLightStatusBars = false
      isAppearanceLightNavigationBars = false
    }
  }

  private fun openSource(source: Source) {
    when (source) {
      Source.CAMERA -> openCamera()
      Source.GALLERY -> pickImageGallery.launch("image/*")
    }
  }

  private fun openCamera() {
    getTmpFileUri().let { uri ->
      latestTmpUri = uri
      takePicture.launch(uri)
    }
  }

  private fun getTmpFileUri(): Uri {
    val dir = File(cacheDir, "share").apply { mkdirs() }
    val tmpFile = File.createTempFile("tmp_image_file", ".png", dir).apply {
      createNewFile()
      deleteOnExit()
    }
    return FileProvider.getUriForFile(
      this,
      "${packageName}.fileprovider",
      tmpFile,
    )
  }

  /**
   * This method show the dialog for user source choice, it is an open function so can be overridden
   * and customized with the app layout if you need.
   * @author udit
   */
  open fun showImageSourceDialog(openSource: (Source) -> Unit) {
    AlertDialog.Builder(this)
      .setCancelable(false)
      .setOnKeyListener { _, keyCode, keyEvent ->
        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
          setResultCancel()
          finish()
        }
        true
      }
      .setTitle(getString(mergedLibraryResId("pick_image_chooser_title", "string")))
      .setItems(
        arrayOf(
          getString(mergedLibraryResId("pick_image_camera", "string")),
          getString(mergedLibraryResId("pick_image_gallery", "string")),
        ),
      ) { _: DialogInterface, which: Int ->
        openSource(if (which == 0) Source.CAMERA else Source.GALLERY)
      }
      .show()
  }

  public override fun onStart() {
    super.onStart()
    cropImageView?.setOnSetImageUriCompleteListener(this)
    cropImageView?.setOnCropImageCompleteListener(this)
  }

  public override fun onStop() {
    super.onStop()
    cropImageView?.setOnSetImageUriCompleteListener(null)
    cropImageView?.setOnCropImageCompleteListener(null)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(BUNDLE_KEY_TMP_URI, latestTmpUri.toString())
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    if (cropImageOptions.skipEditing) return true
    val menuRes = mergedLibraryResId("crop_image_menu", "menu")
    if (menuRes != 0) {
      menuInflater.inflate(menuRes, menu)
    }

    val idRotateLeft = mergedLibraryResId("ic_rotate_left_24", "id")
    val idRotateRight = mergedLibraryResId("ic_rotate_right_24", "id")
    val idFlip = mergedLibraryResId("ic_flip_24", "id")
    val idCrop = mergedLibraryResId("crop_image_menu_crop", "id")

    if (!cropImageOptions.allowRotation) {
      menu.removeItem(idRotateLeft)
      menu.removeItem(idRotateRight)
    } else if (cropImageOptions.allowCounterRotation && idRotateLeft != 0) {
      menu.findItem(idRotateLeft)?.isVisible = true
    }

    if (!cropImageOptions.allowFlipping) menu.removeItem(idFlip)

    if (cropImageOptions.cropMenuCropButtonTitle != null && idCrop != 0) {
      menu.findItem(idCrop)?.title =
        cropImageOptions.cropMenuCropButtonTitle
    }

    var cropIcon: Drawable? = null
    try {
      if (cropImageOptions.cropMenuCropButtonIcon != 0 && idCrop != 0) {
        cropIcon = ContextCompat.getDrawable(this, cropImageOptions.cropMenuCropButtonIcon)
        menu.findItem(idCrop)?.icon = cropIcon
      }
    } catch (e: Exception) {
      Log.w("AIC", "Failed to read menu crop drawable", e)
    }

    if (cropImageOptions.activityMenuIconColor != 0) {
      updateMenuItemIconColor(
        menu,
        idRotateLeft,
        cropImageOptions.activityMenuIconColor,
      )
      updateMenuItemIconColor(
        menu,
        idRotateRight,
        cropImageOptions.activityMenuIconColor,
      )
      updateMenuItemIconColor(menu, idFlip, cropImageOptions.activityMenuIconColor)

      if (cropIcon != null) {
        updateMenuItemIconColor(
          menu,
          idCrop,
          cropImageOptions.activityMenuIconColor,
        )
      }
    }
    cropImageOptions.activityMenuTextColor?.let { menuItemsTextColor ->
      val menuItemIds: List<Int> = listOf(
        idRotateLeft,
        idRotateRight,
        idFlip,
        mergedLibraryResId("ic_flip_24_horizontally", "id"),
        mergedLibraryResId("ic_flip_24_vertically", "id"),
        idCrop,
      )
      for (itemId in menuItemIds) {
        if (itemId != 0) {
          updateMenuItemTextColor(menu, itemId, menuItemsTextColor)
        }
      }
    }
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val idCrop = mergedLibraryResId("crop_image_menu_crop", "id")
    val idRotateLeft = mergedLibraryResId("ic_rotate_left_24", "id")
    val idRotateRight = mergedLibraryResId("ic_rotate_right_24", "id")
    val idFlipH = mergedLibraryResId("ic_flip_24_horizontally", "id")
    val idFlipV = mergedLibraryResId("ic_flip_24_vertically", "id")
    return when (item.itemId) {
      idCrop -> {
        cropImage()
        true
      }
      idRotateLeft -> {
        rotateImage(-cropImageOptions.rotationDegrees)
        true
      }
      idRotateRight -> {
        rotateImage(cropImageOptions.rotationDegrees)
        true
      }
      idFlipH -> {
        cropImageView?.flipImageHorizontally()
        true
      }
      idFlipV -> {
        cropImageView?.flipImageVertically()
        true
      }
      android.R.id.home -> {
        setResultCancel()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  protected open fun onPickImageResult(resultUri: Uri?) {
    when (resultUri) {
      null -> setResultCancel()
      else -> {
        cropImageUri = resultUri
        cropImageView?.setImageUriAsync(cropImageUri)
      }
    }
  }

  override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
    if (error == null) {
      if (cropImageOptions.initialCropWindowRectangle != null) {
        cropImageView?.cropRect = cropImageOptions.initialCropWindowRectangle
      }

      if (cropImageOptions.initialRotation > 0) {
        cropImageView?.rotatedDegrees = cropImageOptions.initialRotation
      }

      if (cropImageOptions.skipEditing) {
        cropImage()
      }
    } else {
      setResult(null, error, 1)
    }
  }

  override fun onCropImageComplete(view: CropImageView, result: CropResult) {
    setResult(result.uriContent, result.error, result.sampleSize)
  }

  /**
   * Execute crop image and save the result tou output uri.
   * @author udit
   */
  open fun cropImage() {
    if (cropImageOptions.noOutputImage) {
      setResult(null, null, 1)
    } else {
      cropImageView?.croppedImageAsync(
        saveCompressFormat = cropImageOptions.outputCompressFormat,
        saveCompressQuality = cropImageOptions.outputCompressQuality,
        reqWidth = cropImageOptions.outputRequestWidth,
        reqHeight = cropImageOptions.outputRequestHeight,
        options = cropImageOptions.outputRequestSizeOptions,
        customOutputUri = cropImageOptions.customOutputUri,
      )
    }
  }

  /**
   * When extending this activity, please set your own ImageCropView
   * @author udit
   */
  open fun setCropImageView(cropImageView: CropImageView) {
    this.cropImageView = cropImageView
  }

  /**
   * Rotate the image in the crop image view.
   * @author udit
   */
  open fun rotateImage(degrees: Int) {
    cropImageView?.rotateImage(degrees)
  }

  /**
   * Result with cropped image data or error if failed.
   * @author udit
   */
  open fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
    setResult(
      if (error != null) CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE else RESULT_OK,
      getResultIntent(uri, error, sampleSize),
    )
    finish()
  }

  /**
   * Cancel of cropping activity.
   * @author udit
   */
  open fun setResultCancel() {
    setResult(RESULT_CANCELED)
    finish()
  }

  /**
   * Get intent instance to be used for the result of this activity.
   * @author udit
   */
  open fun getResultIntent(uri: Uri?, error: Exception?, sampleSize: Int): Intent {
    val result = CropImage.ActivityResult(
      originalUri = cropImageView?.imageUri,
      uriContent = uri,
      error = error,
      cropPoints = cropImageView?.cropPoints,
      cropRect = cropImageView?.cropRect,
      rotation = cropImageView?.rotatedDegrees ?: 0,
      wholeImageRect = cropImageView?.wholeImageRect,
      sampleSize = sampleSize,
    )
    val intent = Intent()
    intent.extras?.let(intent::putExtras)
    intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result)
    return intent
  }

  /**
   * Update the color of a specific menu item to the given color.
   * @author udit
   */
  open fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
    val menuItem = menu.findItem(itemId)
    if (menuItem != null) {
      val menuItemIcon = menuItem.icon
      if (menuItemIcon != null) {
        try {
          menuItemIcon.apply {
            mutate()
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
              color,
              BlendModeCompat.SRC_ATOP,
            )
          }
          menuItem.icon = menuItemIcon
        } catch (e: Exception) {
          Log.w("AIC", "Failed to update menu item color", e)
        }
      }
    }
  }

  /**
   * Update the color of a specific menu item to the given color.
   * @author udit
   */
  open fun updateMenuItemTextColor(menu: Menu, itemId: Int, color: Int) {
    val menuItem = menu.findItem(itemId) ?: return
    val menuTitle = menuItem.title
    if (menuTitle?.isNotBlank() == true) {
      try {
        val spannableTitle: Spannable = SpannableString(menuTitle)
        spannableTitle.setSpan(
          ForegroundColorSpan(color),
          0,
          spannableTitle.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
        menuItem.title = spannableTitle
      } catch (e: Exception) {
        Log.w("AIC", "Failed to update menu item color", e)
      }
    }
  }

  enum class Source { CAMERA, GALLERY }

  private companion object {

    const val BUNDLE_KEY_TMP_URI = "bundle_key_tmp_uri"
  }
}
