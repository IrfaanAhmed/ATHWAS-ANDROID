package com.app.ia.utils

import android.widget.EditText
import androidx.databinding.InverseMethod

object Converter {
    @InverseMethod("convertStringToInt")
    @JvmStatic fun convertStringToInt(
        text: String
    ): Int {
        return text.toInt()
    }

    @InverseMethod("convertStringToDouble")
    @JvmStatic fun convertStringToDouble(
        text: String
    ): Double {
        return text.toDouble()
    }

    @JvmStatic fun convertToDecimalFormat(
        text: String
    ): String {
        return CommonUtils.convertToDecimal(text)
    }

    @JvmStatic fun convertToDecimalFormat(
        text: Double
    ): String {
        return CommonUtils.convertToDecimal(text)
    }

    @JvmStatic fun convertIntToString(
        value: Int
    ): String {
       return "$value"
    }
}