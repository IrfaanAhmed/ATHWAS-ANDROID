package com.app.ia.model

import com.google.gson.annotations.SerializedName;

class FaqResponse(

    @SerializedName("content_data")
    val contentData: MutableList<ContentData>) {

    class ContentData(
        @SerializedName("answer")
        val answer: String,

        @SerializedName("question")
        val question: String,

        @SerializedName("question_id")
        val questionId: String,

        var isCollapse : Boolean = false,

        var viewHeight : Int = 0


    )
}