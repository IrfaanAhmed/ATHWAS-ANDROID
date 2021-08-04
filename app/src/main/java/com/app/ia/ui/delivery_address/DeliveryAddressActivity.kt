package com.app.ia.ui.delivery_address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityDeliveryAddressBinding
import com.app.ia.dialog.IADialog
import com.app.ia.model.AddressListResponse
import com.app.ia.ui.delivery_address.adapter.DeliveryAddressAdapter
import com.app.ia.utils.AppConstants.EXTRA_SELECTED_ADDRESS
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_delivery_address.*
import kotlinx.android.synthetic.main.common_header.view.*

class DeliveryAddressActivity : BaseActivity<ActivityDeliveryAddressBinding, DeliveryAddressViewModel>() {

    private var mBinding: ActivityDeliveryAddressBinding? = null
    private var mViewModel: DeliveryAddressViewModel? = null

    var addressAdapter: DeliveryAddressAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_delivery_address
    }

    override fun getViewModel(): DeliveryAddressViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        mViewModel?.setIntent(intent)

        //makeStatusBarTransparent()
        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.invisible()
        toolbar.ivSearchIcon.invisible()

        //recViewAddress.addItemDecoration(EqualSpacingItemDecoration(0, EqualSpacingItemDecoration.VERTICAL))
        recViewAddress.addItemDecoration(DividerItemDecoration(this@DeliveryAddressActivity, LinearLayout.VERTICAL))
        addressAdapter = DeliveryAddressAdapter(mViewModel?.previousSelectAddressId?.value!!)
        recViewAddress.adapter = addressAdapter
        mViewModel?.addressListResponse?.observe(this, {
            if (it.size <= 0) {
                addressAdapter?.notifyDataSetChanged()
            } else {
                if (addressAdapter?.currentList?.size!! == 0) {
                    addressAdapter?.submitList(it)
                } else {
                    addressAdapter?.notifyDataSetChanged()
                }
            }
        })

        addressAdapter?.setOnAddressClickListener(object : DeliveryAddressAdapter.OnAddressClickListener {
            override fun onAddressSelect(item: AddressListResponse.AddressList, position: Int) {
                if (mViewModel?.isFromHomeScreen!!.value!!) {
                    val intent = Intent()
                    intent.putExtra(EXTRA_SELECTED_ADDRESS, item)
                    intent.putExtra("deletedAddresses", mViewModel?.deletedAddressIds)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            override fun onAddressDelete(item: AddressListResponse.AddressList, position: Int) {

                val iaDialog = IADialog(this@DeliveryAddressActivity, "Are you sure you want to delete this address?", false)
                iaDialog.setOnItemClickListener(object : IADialog.OnClickListener {
                    override fun onPositiveClick() {
                        mViewModel?.deletedPosition!!.value = position
                        val requestParams = HashMap<String, String>()
                        requestParams["address_id"] = item.Id
                        mViewModel?.deleteAddressesObserver(requestParams)
                    }

                    override fun onNegativeClick() {
                    }

                })
            }

            override fun onSetDefaultAddress(item: AddressListResponse.AddressList, position: Int) {
                val requestParams = HashMap<String, String>()
                requestParams["address_id"] = item.Id
                mViewModel?.setDefaultAddressObserver(requestParams)
            }
        })
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(DeliveryAddressViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(DeliveryAddressViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_ADD_ADDRESS && resultCode == RESULT_OK) {
            mViewModel?.getAddressesObserver(HashMap())
        }
    }

    override fun onBackPressed() {
        if (mViewModel?.isFromHomeScreen!!.value!!) {
            val intent = Intent()
            intent.putExtra("deletedAddresses", mViewModel?.deletedAddressIds)
            setResult(Activity.RESULT_CANCELED, intent)
        }
        finish()
    }
}