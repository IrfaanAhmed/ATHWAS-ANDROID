package com.app.ia.ui.edit_profile

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityEditProfileBinding
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>() {

    private var mActivityBinding: ActivityEditProfileBinding? = null
    private var mViewModel: EditProfileViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_edit_profile
    }

    override fun getViewModel(): EditProfileViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)
        mViewModel?.setIntent(intent)

        setOnApplyWindowInset1(toolbar, content_container)

        createImagePicker()
        roundRectView.setOnClickListener {
            mImagePickerManager?.askForPermission()
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(EditProfileViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(EditProfileViewModel::class.java)
    }

    override fun onImageSelect(path: String) {
        super.onImageSelect(path)
        mViewModel?.filePath!!.value = path
        mViewModel?.updateProfileImage()
        //val imgBitmap = BitmapFactory.decodeFile(path)
        //profileImg.setImageBitmap(imgBitmap)
    }
}