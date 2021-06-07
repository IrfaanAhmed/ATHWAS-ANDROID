package com.app.ia.dialog.bottom_sheet_dialog

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseBottomSheetDialogFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.DialogCommonSortBinding
import com.app.ia.dialog.bottom_sheet_dialog.adapter.CommonSortAdapter
import com.app.ia.dialog.bottom_sheet_dialog.viewmodel.CommonSortDialogViewModel
import com.app.ia.model.CommonSortBean
import kotlinx.android.synthetic.main.dialog_common_sort.*

class CommonSortDialogFragment(private val sortOptionList: ArrayList<CommonSortBean>) : BaseBottomSheetDialogFragment<DialogCommonSortBinding, CommonSortDialogViewModel>() {

    private lateinit var mBinding: DialogCommonSortBinding
    private lateinit var mViewModel: CommonSortDialogViewModel
    private var onClickListener: OnSortOptionClickListener? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.dialog_common_sort

    override val viewModel: CommonSortDialogViewModel
        get() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(CommonSortDialogViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(CommonSortDialogViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding!!
        mBinding.lifecycleOwner = this
        mViewModel.setActivityNavigator(this)
        mViewModel.setVariable(mBinding)

        recViewSort.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayout.VERTICAL))
        val commonSortAdapter = CommonSortAdapter()
        recViewSort.adapter = commonSortAdapter
        commonSortAdapter.submitList(sortOptionList)
        commonSortAdapter.setOnItemSelectListener(object : CommonSortAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {

                dismiss()
                var sortValue = ""
                when (position) {
                    0 -> {
                        sortValue = ""
                    }
                    1 -> {
                        sortValue = "price_low_to_high"
                    }
                    2 -> {
                        sortValue = "price_high_to_low"
                    }
                    3 -> {
                        sortValue = "newest"
                    }
                }

                if (onClickListener != null) {
                    onClickListener?.onSortOptionClick(sortValue, position)
                }
            }
        })

        tvCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnItemClickListener(onClickListener: OnSortOptionClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnSortOptionClickListener {
        fun onSortOptionClick(sortValue: String, sortPosition: Int)
    }
}