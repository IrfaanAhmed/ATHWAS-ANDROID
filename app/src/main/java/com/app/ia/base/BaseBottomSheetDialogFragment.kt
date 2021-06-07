package com.app.ia.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.ia.R
import com.app.ia.callback.GeneralCallback
import com.app.ia.image_picker.ImagePickerManager
import com.app.ia.image_picker.OnImagePickListener
import com.app.ia.utils.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding, V : BaseDialogViewModel> : BottomSheetDialogFragment(), GeneralCallback, OnImagePickListener {

    private var baseActivity: BaseActivity<*, *>? = null
    private var mRootView: View? = null
    var viewDataBinding: T? = null
    private var mViewModel: V? = null
    private var mImagePickerManager: ImagePickerManager? = null
    private var progressBar: View? = null

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            this.baseActivity = context
            context.onFragmentAttached()
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = viewDataBinding!!.root
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
        init()
    }

    private fun init() {
        progressBar = layoutInflater.inflate(R.layout.layout_progress, null) as View
        val viewGroup = mRootView as ViewGroup
        viewGroup.addView(progressBar)
    }

    override fun showProgress() {
        requireActivity().runOnUiThread {
            progressBar?.visibility = View.VISIBLE
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            dialog!!.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun hideProgress() {
        if (dialog != null && dialog?.window != null) {
            requireActivity().window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            baseActivity?.runOnUiThread {
                progressBar?.visibility = View.GONE
            }
        }
    }

    fun createImagePicker() {
        mImagePickerManager = ImagePickerManager(this, requireActivity())
        mImagePickerManager?.mFragment = this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mImagePickerManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onImageSelect(path: String) {

    }

    override fun onImageError(message: String) {

    }

    override fun hideKeyboard() {
        CommonUtils.hideSoftInput(requireActivity())
    }

    fun hideKeyboard(view: View) {
        CommonUtils.hideSoftInput(view)
    }
}