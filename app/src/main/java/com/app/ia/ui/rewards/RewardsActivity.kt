package com.app.ia.ui.rewards

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    lateinit var mBinding: ActivityRewardsBinding
    lateinit var mViewModel: RewardsViewModel
    //private lateinit var recyclerViewPaging: RecyclerViewPaginator

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

        /*recyclerViewPaging = object : RecyclerViewPaginator(recViewRewards) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                mViewModel?.redeemPointsObserver()
            }
        }

        recViewRewards.addOnScrollListener(recyclerViewPaging)*/

        mBinding?.recViewRewards?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //super.onScrolled(recyclerView, dx, dy)
                Log.d("Scrolled", "Scrolled")
                val totalItemCount = (mBinding?.recViewRewards.layoutManager as LinearLayoutManager).itemCount
                val lastVisibleItem = (mBinding?.recViewRewards.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                Log.d("Scrolled", "Scrolled ${totalItemCount} $lastVisibleItem")
                if (!mViewModel?.isLoading && mViewModel.isLastPage.value == false && totalItemCount == (lastVisibleItem + 1)) {
                    mViewModel.isLoading = true
                    mViewModel?.currentPage?.value = mViewModel?.currentPage?.value!! + 1
                    //page++
                    mViewModel?.redeemPointsObserver()
                }
            }
        })

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