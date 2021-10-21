package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PromoCodeResponse(

    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("discount_price")
    var discountPrice: String,
    @Expose
    @SerializedName("promo_code")
    val promoCode: String) {

    constructor() : this("", "0.0", "")

    fun getDiscountPriceInFormat(): String{
        return  CommonUtils.convertToDecimal(discountPrice)
    }
}
