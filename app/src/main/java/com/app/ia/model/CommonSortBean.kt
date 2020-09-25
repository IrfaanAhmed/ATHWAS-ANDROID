package com.app.ia.model

class CommonSortBean{

    var sortOption: String = ""
    var isSelected: Boolean = false

    constructor(sortOption: String, isSelected: Boolean): this(){
        this.sortOption = sortOption
        this.isSelected = isSelected
    }

    constructor()
}