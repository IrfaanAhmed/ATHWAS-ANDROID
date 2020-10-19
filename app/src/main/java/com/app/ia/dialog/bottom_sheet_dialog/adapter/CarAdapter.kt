package com.app.ia.dialog.bottom_sheet_dialog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.app.ia.R
import com.app.ia.databinding.CustomSpinnerBinding
import com.app.ia.databinding.SimpleDropdownItemLineBinding
import com.app.ia.model.FilterDataResponse

class CarAdapter(private val mContext: Context, textViewResourceId: Int, private val items: MutableList<FilterDataResponse>) : ArrayAdapter<FilterDataResponse>(mContext, textViewResourceId, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var modelView = convertView
        val binding: CustomSpinnerBinding

        if (modelView == null) {
            binding = CustomSpinnerBinding.inflate(LayoutInflater.from(mContext), parent, false)
            modelView = binding.root
            modelView.tag = binding
        } else {
            binding = modelView.tag as CustomSpinnerBinding
        }

        binding.apply {
            spinnerText.text = items[position].name
            spinnerText.setTextColor(if (position == 0) ContextCompat.getColor(mContext, R.color.light_grey) else ContextCompat.getColor(mContext, (R.color.black)))
            executePendingBindings()
        }

        return modelView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        var dropDownView = convertView
        val binding: SimpleDropdownItemLineBinding

        if (dropDownView == null) {
            binding = SimpleDropdownItemLineBinding.inflate(LayoutInflater.from(mContext), parent, false)
            dropDownView = binding.root
            dropDownView.tag = binding
        } else {
            binding = dropDownView.tag as SimpleDropdownItemLineBinding
        }

        binding.apply {
            text1.text = items[position].name
            text1.setTextColor(if (position == 0) ContextCompat.getColor(mContext, R.color.light_grey) else ContextCompat.getColor(mContext, (R.color.black)))
            executePendingBindings()
        }
        return dropDownView
    }
}