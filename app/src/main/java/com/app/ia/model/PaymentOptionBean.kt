package com.app.ia.model

class PaymentOptionBean{

    var paymentOption: String = ""
    var icon: Int = 0
    var isSelected: Boolean = false

    constructor(paymentOption: String, icon: Int, isSelected: Boolean): this(){
        this.paymentOption = paymentOption
        this.icon = icon
        this.isSelected = isSelected
    }

    constructor()
}