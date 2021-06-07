package com.app.ia.ui.add_new_address

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityAddAddressBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class AddAddressViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    val enteredAddress: ObservableField<String> = ObservableField("")
    val currentAddress: ObservableField<String> = ObservableField("")
    val searchedLocationName: ObservableField<String> = ObservableField("")
    val progressVisible: ObservableField<Boolean> = ObservableField(false)
    val resultList = MutableLiveData<MutableList<MutableMap<String, String>>>()

    val selectedChipValue: ObservableField<Int> = ObservableField(-1)

    var isAddressAdded: Boolean = false

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityAddAddressBinding

    fun setVariable(mBinding: ActivityAddAddressBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.add_new_address))
    }

    fun chipHomeClick() {
        selectedChipValue.set(1)
    }

    fun chipWorkClick() {
        selectedChipValue.set(2)
    }

    fun chipOtherClick() {
        selectedChipValue.set(3)
    }

    fun changeMapType() {
    }

    fun addAddress() {

        baseRepository.callback.hideKeyboard()

        if (mBinding.edtTextAddress.text.toString().isEmpty()) {
            IADialog(mActivity, "Please enter Floor/Apartment No./House No.", true)
            return
        }

        /*if (mBinding.edtTextPinCode.text.toString().isEmpty()) {
            IADialog(mActivity, "Please enter pin code", true)
            return
        }*/

        isAddressAdded = true
        val requestJsonObject = HashMap<String, String>()
        requestJsonObject["full_address"] = currentAddress.get()!!
        requestJsonObject["latitude"] = (mActivity as AddAddressActivity).latitude.toString()
        requestJsonObject["longitude"] = (mActivity as AddAddressActivity).longitude.toString()
        when {
            selectedChipValue.get() == 1 -> requestJsonObject["address_type"] = "Home"
            selectedChipValue.get() == 2 -> requestJsonObject["address_type"] = "Work"
            selectedChipValue.get() == 3 -> requestJsonObject["address_type"] = "Other"
        }
        requestJsonObject["mobile"] = AppPreferencesHelper.getInstance().phone
        requestJsonObject["name"] = AppPreferencesHelper.getInstance().userName
        requestJsonObject["flat"] = ""
        requestJsonObject["location_name"] = ""
        requestJsonObject["building"] = ""
        requestJsonObject["floor"] = mBinding.edtTextAddress.text.toString()
        requestJsonObject["landmark"] = ""
        requestJsonObject["way"] = ""
        requestJsonObject["zip_code"] = mBinding.edtTextPinCode.text.toString()
        addAddressObserver(requestJsonObject)
    }

    fun getAddress(input: String) {
        val params = HashMap<String, String>()
        params["key"] = "AIzaSyC4MhJYysKpRFHe9Re--y5S0_PCtxGir9Q"
        params["input"] = input
        getAddressObserver(params)
    }

    private fun addAddress(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.addAddress(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun addAddressObserver(requestParams: HashMap<String, String>) {

        addAddress(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            val intent = Intent()
                            mActivity.setResult(Activity.RESULT_OK, intent)
                            mActivity.finish()
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun getAddress(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = RetrofitFactory.getGoogleInstance().getAddressFromInput(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun getAddressObserver(requestParams: HashMap<String, String>) {

        getAddress(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            progressVisible.set(false)
                            // Create a JSON object hierarchy from the results
                            val predsJsonArray = users.body()?.predictions

                            // Extract the Place descriptions from the results
                            val finalList: MutableList<MutableMap<String, String>> = ArrayList()
                            for (i in 0 until predsJsonArray?.size!!) {
                                val map: MutableMap<String, String> = HashMap()
                                map["address"] = predsJsonArray[i].description
                                map["place_id"] = predsJsonArray[i].placeId
                                finalList.add(map)
                            }
                            resultList.value = finalList
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        progressVisible.set(false)
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        progressVisible.set(true)
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    /*override fun onSuccess(response: BaseResponse<*>, tag: String) {
    }

    override fun onSuccess(response: JSONObject, tag: String) {

        if (tag == Api.ADD_ADDRESS) {
            if (response.optInt("status_code") == 200) {
                val dialog = CleverDialog(getActivityNavigator()!!, response.optString("message"), true)
                dialog.setOnItemClickListener(object : CleverDialog.OnClickListener {
                    override fun onPositiveClick() {
                        getActivityNavigator()?.onBackPressed()
                    }

                    override fun onNegativeClick() {
                    }
                })
            }
        } else if (tag == "https://maps.googleapis.com/maps/api/place/autocomplete/json") {

            progressVisible.set(false)
            // Create a JSON object hierarchy from the results
            val predsJsonArray = response.optJSONArray("predictions")

            // Extract the Place descriptions from the results
            val finalList: MutableList<MutableMap<String, String>> = ArrayList()
            for (i in 0 until predsJsonArray?.length()!!) {
                val map: MutableMap<String, String> = HashMap()
                map["address"] = predsJsonArray.getJSONObject(i).getString("description")
                map["place_id"] = predsJsonArray.optJSONObject(i).optString("place_id")
                finalList.add(map)
            }
            resultList.value = finalList
        } else {
            val resultsArray: JSONArray = response.optJSONArray("results")!!
            if (resultsArray.length() > 0) {
                val dataObj: JSONObject = resultsArray.optJSONObject(*//*if (resultsArray.length() > 1) 1 else*//* 0)
                currentAddress.set(dataObj.optString("formatted_address"))
            }
        }
    }

    override fun onFailure(errorMsg: String, tag: String) {
        if (tag == "https://maps.googleapis.com/maps/api/place/autocomplete/json") {
            progressVisible.set(false)
        }
    }

    override fun onError(anError: ANError?) {

    }*/
}