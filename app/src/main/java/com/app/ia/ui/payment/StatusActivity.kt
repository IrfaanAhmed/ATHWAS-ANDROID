package com.app.ia.ui.payment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityStatusBinding
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.android.synthetic.main.common_header.view.*

class StatusActivity : BaseActivity<ActivityStatusBinding, StatusViewModel>() {

    private var mBinding: ActivityStatusBinding? = null
    private var mViewModel: StatusViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_status
    }

    override fun getViewModel(): StatusViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!, intent)

        setOnApplyWindowInset(toolbar, content_container)
        toolbar.imageViewIcon.invisible()
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(StatusViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(StatusViewModel::class.java)
    }
}