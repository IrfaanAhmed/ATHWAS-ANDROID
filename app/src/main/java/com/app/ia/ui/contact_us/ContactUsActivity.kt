package com.app.ia.ui.contact_us

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityContactUsBinding
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUsActivity : BaseActivity<ActivityContactUsBinding, ContactUsViewModel>() {

    private var mActivityBinding: ActivityContactUsBinding? = null
    private var mViewModel: ContactUsViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_contact_us
    }

    override fun getViewModel(): ContactUsViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ContactUsViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ContactUsViewModel::class.java)
    }

}