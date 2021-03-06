package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FilterDataResponse() {

    constructor(id: String, name: String) : this() {
        this.id = id
        this.name = name
    }

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}