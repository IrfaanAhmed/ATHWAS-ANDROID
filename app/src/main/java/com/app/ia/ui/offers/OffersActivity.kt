package com.app.ia.ui.offers

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityOffersBinding
import com.app.ia.ui.offers.adapter.OffersAdapter
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.common_header.view.*

class OffersActivity : BaseActivity<ActivityOffersBinding, OffersActivityViewModel>() {

    private var mActivityBinding: ActivityOffersBinding? = null
    private var mViewModel: OffersActivityViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_offers
    }

    override fun getViewModel(): OffersActivityViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)

        setOnApplyWindowInset(toolbar, content_container)
        toolbar.imageViewIcon.invisible()

        val adapter = OffersAdapter(intent.getStringExtra("product_id")!!)
        recViewOffer.adapter = adapter
        val categoryList = ArrayList<String>()
        categoryList.add("Promo Code Offer")
        categoryList.add("Bundle Offer")
        categoryList.add("Promotional Offer")
        categoryList.add("Bank Offer")
        adapter.submitList(categoryList)
    }


    private fun setViewModel() {
        val factory = ViewModelFactory(OffersActivityViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OffersActivityViewModel::class.java)
    }
}