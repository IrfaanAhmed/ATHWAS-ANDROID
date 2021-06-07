package com.app.ia.model

data class FilterData(

    var minPrice: String = "",
    var maxPrice: String = "",
    var rating : String = "",
    var ratingPosition : Int = -1,
    var categoryId : String = "",
    var categoryPos : Int = 0,
    var subCategoryId : String = "",
    var subCategoryPos : Int = 0,
    var brandId : String = "",
    var brandPos : Int = 0,
    var customizationSelectedFilters : HashMap<String, MutableList<CustomizationSubTypeResponse.CustomizationSubType>> = HashMap()

)