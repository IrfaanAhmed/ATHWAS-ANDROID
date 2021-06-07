package com.app.ia.dialog.bottom_sheet_dialog.viewmodel

import androidx.fragment.app.Fragment
import com.app.ia.base.BaseDialogViewModel
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.DialogCommonSortBinding

class CommonSortDialogViewModel(baseRepository: BaseRepository) : BaseDialogViewModel() {

    lateinit var mActivity: Fragment
    lateinit var mBinding: DialogCommonSortBinding

    fun setVariable(mBinding: DialogCommonSortBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }
}