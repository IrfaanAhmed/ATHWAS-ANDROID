package com.app.ia.ui.product_detail.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.PagerAdapter
import com.app.ia.R
import com.app.ia.model.ProductDetailResponse
import com.app.ia.ui.full_image.FullImageActivity
import com.squareup.picasso.Picasso

class ProductImageAdapter(context: Context, images: List<ProductDetailResponse.Product.Images>?) : PagerAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mContext = context
    private var imageList = images

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }

    override fun getCount(): Int {
        return imageList?.size!!
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.full_image_item, view, false)!!

        val imageView = imageLayout.findViewById(R.id.imgViewBanner) as ImageView
        val imageUrl = imageList!![position].productImageUrl
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.get().load(imageUrl).placeholder(R.mipmap.ic_launcher).into(imageView)
        } else {
            Picasso.get().load(R.mipmap.ic_launcher).into(imageView)
        }
        view.addView(imageLayout, 0)

        imageLayout.setOnClickListener {
            val newImageList: ArrayList<String> = ArrayList()
            for(image in imageList!!) {
                newImageList.add(image.productImageUrl)
            }
            val intent = Intent(mContext, FullImageActivity::class.java)
            intent.putStringArrayListExtra("imageArray", newImageList)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, imageLayout, "menu")
            ActivityCompat.startActivity(mContext, intent, options.toBundle())
        }

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