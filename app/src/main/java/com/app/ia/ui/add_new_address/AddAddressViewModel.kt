package com.app.ia.ui.add_new_address

import android.app.Activity
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityAddAddressBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject

class AddAddressViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    val enteredAddress: ObservableField<String> = ObservableField("")
    val searchedLocationName: ObservableField<String> = ObservableField("")
    val progressVisible: ObservableField<Boolean> = ObservableField(false)
    val currentAddress: ObservableField<String> = ObservableField("")
    val resultList = MutableLiveData<MutableList<MutableMap<String, String>>>()

    val selectedChipValue: ObservableField<Int> = ObservableField(-1)
    val isMapSatellite = ObservableBoolean(false)

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

        isAddressAdded = true
        val requestJsonObject = HashMap<String, String>()
        requestJsonObject["full_address"] = currentAddress.get()!!
        requestJsonObject["latitude"] = (mActivity as AddAddressActivity).latitude.toString()
        requestJsonObject["longitude"] =  (mActivity as AddAddressActivity).longitude.toString()
        when {
            selectedChipValue.get() == 1 -> requestJsonObject["address_type"] =  "Home"
            selectedChipValue.get() == 2 -> requestJsonObject["address_type"] = "Work"
            selectedChipValue.get() == 3 -> requestJsonObject["address_type"] = "Other"
        }
        requestJsonObject["mobile"] = AppPreferencesHelper.getInstance().phone
        requestJsonObject["name"] = AppPreferencesHelper.getInstance().firstName
        requestJsonObject["flat"] = ""
        requestJsonObject["location_name"] = ""
        requestJsonObject["building"] = ""
        requestJsonObject["floor"] = ""
        requestJsonObject["landmark"] = ""
        requestJsonObject["way"] = ""
        addAddressObserver(requestJsonObject)
    }

    fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        //baseRepository.googleAddressAPI("https://maps.googleapis.com/maps/api/geocode/json", latitude, longitude, false)
    }

    fun cancelApi() {
        //baseRepository.cancelApiCall("https://maps.googleapis.com/maps/api/geocode/json")
    }

    fun getAddress(input: String) {
        progressVisible.set(true)
        val urlAddress = "https://maps.googleapis.com/maps/api/place/autocomplete/json"
       // baseRepository.googleAddressAPI(urlAddress, input, false)
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
                            if (users.status == "success") {

                            } else {
                                IADialog(mActivity, users.message, true)
                            }
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