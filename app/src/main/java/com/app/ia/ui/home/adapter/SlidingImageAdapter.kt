package com.app.ia.ui.home.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.app.ia.databinding.FullImageItemBinding
import com.app.ia.model.BannerResponse

/**
 * Created by umeshk on 23/04/2016.
 */
class SlidingImageAdapter(context: Context, private val bannerImages: MutableList<BannerResponse.Docs>) : PagerAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var onImageClickListener: OnImageClickListener? = null

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        this.onImageClickListener = onImageClickListener
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }

    override fun getCount(): Int {
        return bannerImages.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val fullImageItemBinding = FullImageItemBinding.inflate(inflater, view, false)
        fullImageItemBinding.apply {
            imageUrl = bannerImages[position].bannerImageUrl
            imgViewBanner.setOnClickListener {
                if (onImageClickListener != null) {
                    onImageClickListener?.onImageClick(it, bannerImages[position])
                }
            }
            executePendingBindings()
        }
        view.addView(fullImageItemBinding.root, 0)
        return fullImageItemBinding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    interface OnImageClickListener {
        fun onImageClick(view: View, image: BannerResponse.Docs)
    }
}