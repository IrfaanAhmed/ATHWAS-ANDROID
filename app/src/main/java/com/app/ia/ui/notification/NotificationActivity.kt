package com.app.ia.ui.notification

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityNotificationBinding
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.RecyclerViewPaginator
import com.app.ia.utils.setOnApplyWindowInset1
import com.app.ia.utils.visible
import com.app.wallet.tivo.ui.notification.adapter.NotificationAdapter
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.common_header.view.*

class NotificationActivity : BaseActivity<ActivityNotificationBinding, NotificationViewModel>() {

    private var mActivityBinding: ActivityNotificationBinding? = null
    private var mViewModel: NotificationViewModel? = null
    lateinit var mNotificationAdapter: NotificationAdapter
    lateinit var recyclerViewPaging: RecyclerViewPaginator

    companion object {

        const val NOTIFICATION_LIST = 1
        const val DELETE_NOTIFICATION = 2
        const val DELETE_ALL_NOTIFICATION = 3
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }

    override fun getViewModel(): NotificationViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)
        toolbar.ivEditProfileIcon.visible()

        notificationRecyclerView.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))

        mSwipeRefresh.setOnRefreshListener {
            resetNotification()
        }

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(NotificationViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(NotificationViewModel::class.java)
    }

    fun resetNotification() {

        mViewModel?.isLastPage!!.value = false
        mViewModel?.currentPage?.value = 1
        mViewModel?.notificationListData?.value = ArrayList()

        mNotificationAdapter = NotificationAdapter()
        notificationRecyclerView.adapter = mNotificationAdapter

        if (mSwipeRefresh.isRefreshing) {
            mSwipeRefresh.isRefreshing = false
        }

        if (::recyclerViewPaging.isInitialized) {
            recyclerViewPaging.reset()
        }

        recyclerViewPaging = object : RecyclerViewPaginator(notificationRecyclerView) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                val requestParams = HashMap<String, String>()
                requestParams["page_no"] = start.toString()
                //mViewModel?.setupObservers(null, requestParams, NOTIFICATION_LIST, -1)
            }
        }

        notificationRecyclerView.addOnScrollListener(recyclerViewPaging)

        mViewModel?.notificationListData?.observe(this, {
            mNotificationAdapter.submitList(it)
        })

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = mViewModel?.currentPage?.value.toString()
        //mViewModel?.setupObservers(null, requestParams, NOTIFICATION_LIST, -1)
    }
}