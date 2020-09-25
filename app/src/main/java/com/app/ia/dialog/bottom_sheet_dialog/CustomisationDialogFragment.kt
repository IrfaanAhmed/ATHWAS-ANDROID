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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.dialog.bottom_sheet_dialog.adapter.CommonSortAdapter
import com.app.ia.dialog.bottom_sheet_dialog.adapter.CustomisationAdapter
import com.app.ia.dialog.bottom_sheet_dialog.adapter.PaymentOptionAdapter
import com.app.ia.model.CommonSortBean
import com.app.ia.model.CustomisationBean
import com.app.ia.model.PaymentOptionBean
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_customization.*


class CustomisationDialogFragment(val customisationList: ArrayList<CustomisationBean>) : BottomSheetDialogFragment(){

    private var onClickListener: OnAddMoneyClickListener? = null

    fun setOnItemClickListener(onClickListener: OnAddMoneyClickListener) {
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
        return inflater.inflate(R.layout.dialog_customization, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDone.setOnClickListener { dismiss() }

        recViewSize.layoutManager  = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        var adapter = CustomisationAdapter()
        recViewSize.adapter = adapter
        adapter.submitList(customisationList)
        adapter.setOnItemSelectListener(object: CustomisationAdapter.OnItemSelectListener{
            override fun onItemSelect(position: Int) {
                for(index in customisationList.indices){
                    customisationList[index].isSelected = index == position
                }
                adapter.notifyDataSetChanged()
            }

        })
    }

    interface OnAddMoneyClickListener {
        fun onAddMoneyClick(data: String)
    }

}