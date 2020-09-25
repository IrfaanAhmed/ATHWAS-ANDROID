package com.app.ia.ui.product_detail.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.app.ia.R
import com.squareup.picasso.Picasso

class ProductImageAdapter(context: Context, images: MutableList<String>) : PagerAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mContext = context
    private var imageList = images

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.full_image_item, view, false)!!

        val imageView = imageLayout.findViewById(R.id.imgViewBanner) as ImageView
        val imageUrl = imageList[position]
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.mobile).into(imageView)
        } else {
            Picasso.get().load(R.drawable.mobile).into(imageView)
        }
        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}