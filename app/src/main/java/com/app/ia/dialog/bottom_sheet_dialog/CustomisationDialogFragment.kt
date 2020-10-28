package com.app.ia.dialog.bottom_sheet_dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.ia.R
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.dialog.bottom_sheet_dialog.adapter.CustomisationAdapter
import com.app.ia.model.CustomizationProductDetail
import com.app.ia.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_customization.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class CustomisationDialogFragment(private val mySelectedCustomizationPos: Int, private val mainProductId: String) : BottomSheetDialogFragment() {

    private var onClickListener: OnCustomizationSelectListener? = null

    val adapter = CustomisationAdapter()
    val customizationList = ArrayList<CustomizationProductDetail.Data>()

    fun setOnItemClickListener(onClickListener: OnCustomizationSelectListener) {
        this.onClickListener = onClickListener
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.dialog_customization, container, false)
    }

    var selectedCustomizationPos = -1
    var selectedCustomizationId = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedCustomizationPos = mySelectedCustomizationPos

        tvDone.setOnClickListener {

            if (selectedCustomizationPos != -1) {
                if (onClickListener != null) {
                    onClickListener?.onCustomizationSelect(selectedCustomizationId, selectedCustomizationPos)
                }
            }
            dismiss()
        }

        recViewSize.adapter = adapter

        callCustomizationProductDetail(mainProductId)
        adapter.setOnItemSelectListener(object : CustomisationAdapter.OnItemSelectListener {
            override fun onItemSelect(item: CustomizationProductDetail.Data, position: Int) {

                selectedCustomizationPos = position
                selectedCustomizationId = item.Id
                for (i in 0 until customizationList.size) {
                    customizationList[i].isSelected = (position == i)
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    interface OnCustomizationSelectListener {
        fun onCustomizationSelect(id: String, pos: Int)
    }

    private fun callCustomizationProductDetail(main_product_id: String) {
        val requestParams = HashMap<String, String>()
        requestParams["main_product_id"] = main_product_id

        val service = RetrofitFactory.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getCustomizationProductDetail(requestParams)

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //Do something with response e.g show to the UI.
                        customizationList.addAll(response.body()?.data!!)
                        if (customizationList.size > 0) {
                            adapter.submitList(customizationList)
                        }

                        if (customizationList.size > selectedCustomizationPos) {
                            customizationList[selectedCustomizationPos].isSelected = true
                        }
                        adapter.notifyDataSetChanged()

                    } else {
                        requireActivity().toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    //requireActivity().toast("Exception ${e.message}")
                } catch (e: Throwable) {
                    //requireActivity().toast("Oops: Something else went wrong")
                }
            }
        }
    }
}