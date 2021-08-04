package com.app.ia.ui.full_image

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityFullImageBinding
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_image.*

class FullImageActivity : BaseActivity<ActivityFullImageBinding, FullImageViewModel>() {

    private var mActivityFullImageBinding: ActivityFullImageBinding? = null
    private var mFullImageViewModel: FullImageViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_full_image
    }

    override fun getViewModel(): FullImageViewModel {
        return mFullImageViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)

        mActivityFullImageBinding = getViewDataBinding()
        mActivityFullImageBinding!!.lifecycleOwner = this
        mFullImageViewModel?.setActivityNavigator(this)
        mFullImageViewModel?.setVariable(mActivityFullImageBinding!!)

        val images = intent?.getStringArrayListExtra("imageArray")!!
        view_pager.adapter = TouchImageAdapter(images)
        view_pager.post {
            view_pager.setCurrentItem(intent?.getIntExtra("SelectedPos", 0)!!, false)
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(FullImageViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mFullImageViewModel = ViewModelProvider(this, factory).get(FullImageViewModel::class.java)
    }

    internal class TouchImageAdapter(private var images: ArrayList<String>) : PagerAdapter() {

        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val img = TouchImageView(container.context)

            Picasso.get().load(images[position]).placeholder(R.mipmap.ic_launcher).into(img)

            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            img.tag = "image"
            return img
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAfterTransition(this)
    }
}