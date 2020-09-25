package com.app.ia.base

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.ia.IAApplication
import com.app.ia.R
import com.app.ia.callback.GeneralCallback
import com.app.ia.image_picker.ImagePickerManager
import com.app.ia.image_picker.OnImagePickListener
import com.app.ia.utils.CommonUtils
import com.app.ia.utils.LocationManager
import com.example.easywaylocation.LocationData

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> : AppCompatActivity(), GeneralCallback, BaseFragment.Callback, OnImagePickListener, LocationManager.CurrentLocationListener {

    private var progressBar: View? = null
    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null
    var mImagePickerManager: ImagePickerManager? = null
    var mLocationManager: LocationManager? = null
    private var isLocationSelected = false

    override fun onResume() {
        super.onResume()
        IAApplication.getInstance().setCurrentActivity(this)
    }

    override fun onPause() {
        super.onPause()
        IAApplication.getInstance().setCurrentActivity(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        performDataBinding()
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    fun getViewDataBinding(): T {
        return mViewDataBinding!!
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V

    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        this.mViewModel = if (mViewModel == null) getViewModel() else mViewModel
        mViewDataBinding?.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding?.executePendingBindings()
    }

    private fun init() {
        progressBar = layoutInflater.inflate(R.layout.layout_progress, null) as View
        val v = this.findViewById<View>(android.R.id.content).rootView
        val viewGroup = v as ViewGroup
        viewGroup.addView(progressBar)
    }

    override fun showProgress() {
        runOnUiThread {
            progressBar?.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun hideProgress() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        runOnUiThread {
            progressBar?.visibility = View.GONE
        }
    }

    fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }

    fun currentLocationManager(requireLastLocation: Boolean = true) {
        isLocationSelected = true
        mLocationManager = LocationManager(this, this, requireLastLocation)
        mLocationManager?.checkPermission()
    }

    fun createImagePicker() {
        mImagePickerManager = ImagePickerManager(this, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isLocationSelected) {
            mLocationManager?.onActivityResult(requestCode, resultCode)
        } else {
            mImagePickerManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onImageSelect(path: String) {

    }

    override fun onImageError(message: String) {

    }

    override fun hideKeyboard() {
        CommonUtils.hideSoftInput(this)
    }

    fun hideKeyboard(view: View) {
        CommonUtils.hideSoftInput(view)
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {

    }

    /*fun loginDialog() {
        val activity = this
        val cleverDialog = CleverDialog(activity, "", getString(R.string.need_login_msg), getString(R.string.continues), getString(R.string.cancel), false)
        cleverDialog.setOnItemClickListener(object : CleverDialog.OnClickListener {
            override fun onPositiveClick() {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("isFromOtherScreen", true)
                startActivityForResult(intent, AppRequestCode.REQUEST_LOGIN)
            }

            override fun onNegativeClick() {
            }
        })
    }*/

    /*fun comingSoonDialog() {
        CleverDialog(this, getString(R.string.soon), true)
    }

    fun cleverGoldDialog() {
        CleverDialog(this, getString(R.string.gold_coming_soon_msg), true)
    }*/

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            mLocationManager?.removeLocationUpdate()
        }
    }

    override fun onBackPressed() {
        if (progressBar!!.visibility == View.GONE) {
            super.onBackPressed()
        }
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {

    }

    override fun onLocationUpdate(locationResult: Location?) {

    }

    override fun onLocationFetchFailed() {

    }

    override fun onAddressUpdate(locationData: LocationData?) {

    }

}