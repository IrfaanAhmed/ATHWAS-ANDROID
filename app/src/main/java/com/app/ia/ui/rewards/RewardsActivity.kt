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
import com.app.ia.utils.RecyclerViewPaginator
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_rewards.*
import kotlinx.android.synthetic.main.activity_rewards.content_container
import kotlinx.android.synthetic.main.activity_rewards.toolbar
import kotlinx.android.synthetic.main.common_header.view.*

class RewardsActivity : BaseActivity<ActivityRewardsBinding, RewardsViewModel>() {

    private var mBinding: ActivityRewardsBinding? = null
    private var mViewModel: RewardsViewModel? = null
    private lateinit var recyclerViewPaging: RecyclerViewPaginator

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

        recyclerViewPaging = object : RecyclerViewPaginator(recViewRewards) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                mViewModel?.redeemPointsObserver()
            }
        }

        recViewRewards.addOnScrollListener(recyclerViewPaging)

        mViewModel?.promoCodeListData?.observe(this, {
            if (rewardAdapter.currentList.size == 0) {
                rewardAdapter.submitList(it)
            } else {
                rewardAdapter.notifyDataSetChanged()
            }
        })

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(RewardsViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(RewardsViewModel::class.java)
    }
}