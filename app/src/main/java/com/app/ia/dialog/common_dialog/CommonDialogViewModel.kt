package com.app.ia.dialog.common_dialog

import androidx.databinding.ObservableField
import com.app.ia.base.BaseDialogViewModel

class CommonDialogViewModel(private val commonDialogNavigator: CommonDialogNavigator) : BaseDialogViewModel() {

    val heading: ObservableField<String> = ObservableField("")
    val message: ObservableField<String> = ObservableField("")
    val positiveText: ObservableField<String> = ObservableField("")
    val negativeText: ObservableField<String> = ObservableField("")

    fun onPositiveClick() {
        commonDialogNavigator.onPositiveClick()
    }

    fun onNegativeClick() {
        commonDialogNavigator.onNegativeClick()
    }
}