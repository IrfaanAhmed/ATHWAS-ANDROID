package com.app.ia.ui.write_review

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityWriteReviewBinding
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_write_review.*
import kotlinx.android.synthetic.main.common_header.view.*

class WriteReviewActivity : BaseActivity<ActivityWriteReviewBinding, WriteReviewViewModel>() {

    private var mBinding: ActivityWriteReviewBinding? = null
    private var mViewModel: WriteReviewViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_write_review
    }

    override fun getViewModel(): WriteReviewViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!, intent)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.gone()
        toolbar.ivSearchIcon.gone()
        toolbar.ivEditProfileIcon.gone()

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(WriteReviewViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(WriteReviewViewModel::class.java)
    }
}