package com.app.ia.model

class BusinessCategoryBean {

    var name: String = ""
    var icon: Int = 0

    constructor(name: String, icon: Int) : this() {
        this.name = name
        this.icon = icon
    }

    constructor()
}