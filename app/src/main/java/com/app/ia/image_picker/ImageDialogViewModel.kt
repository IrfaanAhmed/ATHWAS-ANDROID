package com.app.ia.image_picker

import com.app.ia.base.BaseDialogViewModel
import com.app.ia.image_picker.ImagePickerDialogNavigator

class ImageDialogViewModel(private val imagePickerDialogNavigator: ImagePickerDialogNavigator?) : BaseDialogViewModel() {

    fun onCameraClick() {
        imagePickerDialogNavigator?.onCameraClick()
    }

    fun onGalleryClick() {
        imagePickerDialogNavigator?.onGalleryClick()
    }
}