package com.app.ia.dialog.bottom_sheet_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ia.R
import com.app.ia.utils.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_payment_select.*


class PaymentSelectDialogFragment : BottomSheetDialogFragment() {

    private var onClickListener: OnWalletFilterClickListener? = null
    private var selectedPaymentMethod = ""
    private var walletAmount = ""

    fun setOnItemClickListener(onClickListener: OnWalletFilterClickListener) {
        this.onClickListener = onClickListener
    }

    companion object {
        fun newInstance(value: String?, walletAmount: String): PaymentSelectDialogFragment {
            val args = Bundle()
            val fragment = PaymentSelectDialogFragment()
            args.putString("selectedPaymentMethod", value)
            args.putString("walletAmount", walletAmount)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedPaymentMethod = arguments?.getString("selectedPaymentMethod", "")!!
        walletAmount = CommonUtils.convertToDecimal(arguments?.getString("walletAmount", "0.0")!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.dialog_payment_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {

        txtViewAvailableCredit.text = "Available Credit : " + String.format(getString(R.string.inr_price), CommonUtils.convertToDecimal(walletAmount))

        ivClose.setOnClickListener {
            dismiss()
        }

        when (selectedPaymentMethod) {
            "Cash" -> {
                radioBtnCOD.isChecked = true
            }
            "Wallet" -> {
                radioBtnWallet.isChecked = true
            }
            "Credit" -> {
                radioBtnCredit.isChecked = true
            }
        }

        rgFilter.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.radioBtnCOD -> {
                    selectedPaymentMethod = "Cash"
                }

                R.id.radioBtnWallet -> {
                    selectedPaymentMethod = "Wallet"
                }

                R.id.radioBtnCredit -> {
                    selectedPaymentMethod = "Credit"
                }
            }
        }

        buttonApply.setOnClickListener {
            onClickListener!!.onSubmitClick(selectedPaymentMethod)
            dismiss()
        }
    }

    interface OnWalletFilterClickListener {
        fun onSubmitClick(selectedMethod: String)
    }
}