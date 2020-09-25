package com.app.ia.dialog.bottom_sheet_dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.app.ia.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_product_filter.*


class ProductFilterDialogFragment : BottomSheetDialogFragment(){

    private var onClickListener: OnProductFilterClickListener? = null
    private var filterValue = ""

    fun setOnItemClickListener(onClickListener: OnProductFilterClickListener) {
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
        return inflater.inflate(R.layout.dialog_product_filter, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUp() {

        tvRateFirst.setOnClickListener {
            setClickedAction(tvRateFirst)
        }

        tvRateSecond.setOnClickListener {
            setClickedAction(tvRateSecond)
        }

        tvRateThird.setOnClickListener {
            setClickedAction(tvRateThird)
        }

        tvRateFourth.setOnClickListener {
            setClickedAction(tvRateFourth)
        }

        tvRateFifth.setOnClickListener {
            setClickedAction(tvRateFifth)
        }

        tvCancel.setOnClickListener {
            dismiss()
        }

        /* buttonApply.setOnClickListener {
            if(filterValue.isEmpty()){
                TivoDialog(requireActivity(), getString(R.string.please_select_filter_option), true)
                return@setOnClickListener
            }
            onClickListener!!.onSubmitClick(filterValue)
            dismiss()
        }*/
    }

    interface OnProductFilterClickListener {
        fun onSubmitClick(filterValue: String)
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setClickedAction(textView: AppCompatTextView){
        if(textView.id == R.id.tvRateFirst){
            tvRateFirst.background = ContextCompat.getDrawable(requireContext(), R.drawable.primary_color_fill_gradient)
            tvRateFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tvRateFirst.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

            tvRateSecond.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateSecond.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateSecond.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateThird.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateThird.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateThird.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFourth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFourth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFourth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFifth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFifth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFifth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
        }else if(textView.id == R.id.tvRateSecond){
            tvRateSecond.background = ContextCompat.getDrawable(requireContext(), R.drawable.primary_color_fill_gradient)
            tvRateSecond.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tvRateSecond.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

            tvRateFirst.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFirst.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateThird.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateThird.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateThird.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFourth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFourth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFourth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFifth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFifth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFifth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
        }else if(textView.id == R.id.tvRateThird){
            tvRateThird.background = ContextCompat.getDrawable(requireContext(), R.drawable.primary_color_fill_gradient)
            tvRateThird.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tvRateThird.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

            tvRateFirst.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFirst.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateSecond.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateSecond.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateSecond.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFourth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFourth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFourth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFifth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFifth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFifth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
        }else if(textView.id == R.id.tvRateFourth){
            tvRateFourth.background = ContextCompat.getDrawable(requireContext(), R.drawable.primary_color_fill_gradient)
            tvRateFourth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tvRateFourth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

            tvRateFirst.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFirst.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateSecond.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateSecond.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateSecond.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateThird.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateThird.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateThird.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFifth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFifth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFifth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
        }else if(textView.id == R.id.tvRateFifth){
            tvRateFifth.background = ContextCompat.getDrawable(requireContext(), R.drawable.primary_color_fill_gradient)
            tvRateFifth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tvRateFifth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

            tvRateFirst.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFirst.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateSecond.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateSecond.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateSecond.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateThird.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateThird.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateThird.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))

            tvRateFourth.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
            tvRateFourth.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tvRateFourth.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
        }
    }
}