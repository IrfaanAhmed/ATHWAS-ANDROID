package com.app.ia.image_picker

interface OnImagePickListener {

    fun onImageSelect(path: String)

    fun onImageError(message: String)
}
