package com.app.ia.ui.place_picker

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityPlacePickerBinding
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_place_picker.*
import kotlinx.android.synthetic.main.common_header.view.*

class PlacePickerActivity : BaseActivity<ActivityPlacePickerBinding, PlacePickerViewModel>() {

    private var mBinding: ActivityPlacePickerBinding? = null
    private var mViewModel: PlacePickerViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_place_picker
    }

    override fun getViewModel(): PlacePickerViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.invisible()

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(PlacePickerViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(PlacePickerViewModel::class.java)
    }
}