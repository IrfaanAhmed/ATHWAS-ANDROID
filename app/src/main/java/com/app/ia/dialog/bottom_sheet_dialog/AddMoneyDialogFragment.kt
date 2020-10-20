package com.app.ia.dialog.bottom_sheet_dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.ia.R
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.dialog.IADialog
import com.app.ia.dialog.bottom_sheet_dialog.adapter.PaymentOptionAdapter
import com.app.ia.dialog.bottom_sheet_dialog.adapter.TopUpValueAdapter
import com.app.ia.model.PaymentOptionBean
import com.app.ia.ui.home.HomeActivity
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_add_money.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AddMoneyDialogFragment(val paymentOptionList: ArrayList<PaymentOptionBean>) : BottomSheetDialogFragment() {

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
        return inflater.inflate(R.layout.dialog_add_money, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCancel.setOnClickListener { dismiss() }

        val paymentOptionAdapter = PaymentOptionAdapter()
        recPaymentMethod.adapter = paymentOptionAdapter
        paymentOptionAdapter.submitList(paymentOptionList)
        paymentOptionAdapter.setOnItemSelectListener(object : PaymentOptionAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {
                for (index in paymentOptionList.indices) {
                    paymentOptionList[index].isSelected = index == position
                }
                paymentOptionAdapter.notifyDataSetChanged()
            }
        })

        // For Top Up Value RecyclerView
        val topUpValueList = ArrayList<String>()
        topUpValueList.add("20")
        topUpValueList.add("50")
        topUpValueList.add("100")
        topUpValueList.add("300")
        topUpValueList.add("500")
        topUpValueList.add("700")
        topUpValueList.add("1000")

        val mEqualSpacingItemDecoration = EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.HORIZONTAL)
        recyclerViewValue.addItemDecoration(mEqualSpacingItemDecoration)

        val mTopUpValueAdapter = TopUpValueAdapter(this)
        recyclerViewValue.adapter = mTopUpValueAdapter
        mTopUpValueAdapter.submitList(topUpValueList)

        edtTextAmount.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.isNotEmpty()!! && mTopUpValueAdapter.selectedPosition != -1) {
                    mTopUpValueAdapter.selectedPosition = -1
                    mTopUpValueAdapter.notifyDataSetChanged()
                }
            }
        })

        buttonAddMoney.setOnClickListener {

            if (mTopUpValueAdapter.selectedPosition == -1 && edtTextAmount.text.toString().isEmpty()) {
                IADialog(requireActivity(), getString(R.string.please_enter_amount), true)
            } else {
                val amount: String = if (mTopUpValueAdapter.selectedPosition == -1) {
                    edtTextAmount.text.toString()
                } else {
                    topUpValueList[mTopUpValueAdapter.selectedPosition]
                }
                addToMoney(amount)
            }
        }
    }

    fun clearAmountValue() {
        if (requireActivity() is HomeActivity) {
            (requireActivity() as HomeActivity).hideKeyboard(edtTextAmount)
        }
        edtTextAmount.setText("")
    }

    interface OnAddMoneyClickListener {
        fun onAddMoneyClick(data: String)
    }

    private fun addToMoney(amount: String) {
        val requestParams = HashMap<String, String>()
        requestParams["amount"] = amount
        requestParams["reason"] = ""

        val service = RetrofitFactory.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.addToWallet(requestParams)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //Do something with response e.g show to the UI.
                        dismiss()
                        val walletResponse = response.body()!!
                        requireActivity().toast(walletResponse.message)
                        if (onClickListener != null) {
                            onClickListener?.onAddMoneyClick(walletResponse.data?.wallet!!)
                        }
                    } else {
                        requireActivity().toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    requireActivity().toast("Exception ${e.message}")
                } catch (e: Throwable) {
                    requireActivity().toast("Oops: Something else went wrong")
                }
            }
        }
    }

}