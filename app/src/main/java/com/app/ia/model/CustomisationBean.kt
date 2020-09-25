package com.app.ia.model

class CustomisationBean{

    var sizeValue: String = ""
    var isSelected: Boolean = false

    constructor(sizeValue: String, isSelected: Boolean): this(){
        this.sizeValue = sizeValue
        this.isSelected = isSelected
    }

    constructor()
}