package com.app.ia.ui.track_order

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.IAApplication
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityTrackOrderBinding
import com.app.ia.utils.invisible
import com.app.ia.utils.redact
import com.app.ia.utils.setOnApplyWindowInset1
import com.app.ia.utils.toast
import com.transferwise.sequencelayout.SequenceStep
import kotlinx.android.synthetic.main.activity_track_order.*
import kotlinx.android.synthetic.main.common_header.view.*
import java.text.SimpleDateFormat
import java.util.*

class TrackOrderActivity : BaseActivity<ActivityTrackOrderBinding, TrackOrderViewModel>() {

    private var mBinding: ActivityTrackOrderBinding? = null
    private var mViewModel: TrackOrderViewModel? = null

    private var lastOrderTrackingStatus = ""

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_track_order
    }

    override fun getViewModel(): TrackOrderViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!, intent)
        lastOrderTrackingStatus = intent.getStringExtra("orderStatus")!!

        setOnApplyWindowInset1(toolbar, content_container)
        toolbar.imageViewIcon.invisible()

        mViewModel?.orderDetailResponse?.observe(this, {
            var activePosition = 0

            if (it.trackingStatus.acknowledged.status == 1) {
                activePosition = 0
            }
            if (it.trackingStatus.packed.status == 1) {
                activePosition = 1
            }
            if (it.trackingStatus.inTransit.status == 1) {
                activePosition = 2
            }
            if (it.trackingStatus.delivered.status == 1) {
                activePosition = 3
            }

            //Sequence Layout
            if (trackLayout != null) {
                trackLayout.removeAllSteps()
            }

            showTracking(it.trackingStatus.acknowledged.statusTitle, it.trackingStatus.acknowledged.time, activePosition == 0)
            showTracking(it.trackingStatus.packed.statusTitle, it.trackingStatus.packed.time, activePosition == 1)
            showTracking(it.trackingStatus.inTransit.statusTitle, it.trackingStatus.inTransit.time, activePosition == 2)
            showTracking(it.trackingStatus.delivered.statusTitle, it.trackingStatus.delivered.time, activePosition == 3)
            trackLayout.start()
        })

        mSwipeRefresh.setOnRefreshListener {
            if (mSwipeRefresh.isRefreshing) {
                mSwipeRefresh.isRefreshing = false
            }
            mViewModel?.orderDetailObserver(intent.getStringExtra("order_id")!!.toString())
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(TrackOrderViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(TrackOrderViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        refresh()
        IAApplication.getInstance().setCurrentActivity(this)
    }

    override fun onStart() {
        super.onStart()
        IAApplication.getInstance().setCurrentActivity(this)
    }

    override fun onRestart() {
        super.onRestart()
        IAApplication.getInstance().setCurrentActivity(this)
    }

    override fun onStop() {
        super.onStop()
        IAApplication.getInstance().setCurrentActivity(null)
    }

    fun refresh() {
        mViewModel?.orderDetailObserver(intent.getStringExtra("order_id")!!.toString())
    }

    private fun showTracking(title: String, trackTime: String?, isActive: Boolean) {
        val sequenceView = SequenceStep(this)
        val param = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        sequenceView.setAnchorTextAppearance(R.style.stepSubTitle)
        sequenceView.setTitleTextAppearance(R.style.stepTitle)
        sequenceView.setSubtitleTextAppearance(R.style.stepSubTitle)
        sequenceView.layoutParams = param
        if (trackTime.isNullOrEmpty()) {
            sequenceView.setAnchor("")
        } else {
            sequenceView.setAnchor(getTrackTime(trackTime))
        }
        sequenceView.setSubtitle("")
        sequenceView.setTitle(title)
        sequenceView.setActive(isActive)
        trackLayout.addView(sequenceView)
    }

    private fun getTrackTime(trackTime: String): String {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputDate: String
        outputDate = try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value: Date = formatter.parse(trackTime)!!
            val timeZone = TimeZone.getDefault()
            val dateFormatter = SimpleDateFormat("dd MMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
            dateFormatter.timeZone = timeZone
            dateFormatter.format(value)
        } catch (e: Exception) {
            if (trackTime.isEmpty()) {
                "01 May 2020, 10:00 AM"
            } else {
                trackTime
            }
        }
        return outputDate
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("isStatusChanged", (lastOrderTrackingStatus != mViewModel?.orderDetailResponse?.value?.currentTrackingStatus))
        setResult(RESULT_OK, intent)
        finish()
    }
}