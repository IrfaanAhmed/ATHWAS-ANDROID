package com.app.ia.ui.rewards

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityRewardsBinding
import com.app.ia.ui.rewards.adapter.RewardListAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_rewards.*
import kotlinx.android.synthetic.main.common_header.view.*

class RewardsActivity : BaseActivity<ActivityRewardsBinding, RewardsViewModel>() {

    private var mBinding: ActivityRewardsBinding? = null
    private var mViewModel: RewardsViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rewards
    }

    override fun getViewModel(): RewardsViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        //makeStatusBarTransparent()
        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.invisible()

        recViewRewards.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))

        val rewardAdapter = RewardListAdapter()
        recViewRewards.adapter = rewardAdapter
        val categoryList = ArrayList<String>()
        categoryList.add("")
        categoryList.add("")
        categoryList.add("")
        categoryList.add("")
        rewardAdapter.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(RewardsViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(RewardsViewModel::class.java)
    }
}