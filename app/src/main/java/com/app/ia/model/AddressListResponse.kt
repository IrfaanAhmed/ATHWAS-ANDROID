package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddressListResponse(

    @Expose
    @SerializedName("addresslist")
    var addresslist: MutableList<AddressList>): Serializable {

    data class AddressList(
        @Expose
        @SerializedName("type")
        val type: String,
        @Expose
        @SerializedName("geoLocation")
        var geolocation: Geolocation,
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("user_id")
        val userId: String,
        @Expose
        @SerializedName("location_name")
        val locationName: String,
        @Expose
        @SerializedName("mobile")
        val mobile: String,
        @Expose
        @SerializedName("floor")
        val floor: String,
        @Expose
        @SerializedName("way")
        val way: String,
        @Expose
        @SerializedName("building")
        val building: String,
        @Expose
        @SerializedName("address_type")
        val addressType: String,
        @Expose
        @SerializedName("flat")
        val flat: String,
        @Expose
        @SerializedName("landmark")
        val landmark: String,
        @Expose
        @SerializedName("full_address")
        val fullAddress: String): Serializable {

        data class Geolocation(
            @Expose
            @SerializedName("type")
            val type: String,
            @Expose
            @SerializedName("coordinates")
            var coordinates: List<Double>,
            @Expose
            @SerializedName("_id")
            val Id: String): Serializable{

        }
    }
}