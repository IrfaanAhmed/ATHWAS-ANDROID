package com.app.ia.dialog.bottom_sheet_dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.R
import com.app.ia.dialog.bottom_sheet_dialog.adapter.CommonSortAdapter
import com.app.ia.model.CommonSortBean
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_common_sort.*


class CommonSortDialogFragment(val sortOptionList: ArrayList<CommonSortBean>) : BottomSheetDialogFragment() {

    private var onClickListener: OnSortOptionClickListener? = null

    fun setOnItemClickListener(onClickListener: OnSortOptionClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.dialog_common_sort, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recViewSort.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayout.VERTICAL))
        val commonSortAdapter = CommonSortAdapter()
        recViewSort.adapter = commonSortAdapter
        commonSortAdapter.submitList(sortOptionList)
        commonSortAdapter.setOnItemSelectListener(object : CommonSortAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {
                dismiss()
            }
        })
    }

    interface OnSortOptionClickListener {
        fun onSortOptionClick(filterValue: String)
    }

}