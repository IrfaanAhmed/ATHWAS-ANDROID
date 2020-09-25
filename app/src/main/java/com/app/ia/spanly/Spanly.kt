package com.app.ia.spanly

import android.text.SpannableStringBuilder
import com.app.ia.spanly.internal.helpers.span

/**
 * Created by Joshua de Guzman on 2/11/19.
 */
class Spanly : SpannableStringBuilder() {

    companion object {
        val TAG: String = Spanly::class.java.simpleName
    }

    override fun append(text: CharSequence): Spanly {
        super.append(text)
        return this
    }

    fun append(text: CharSequence, vararg spans: Any): Spanly {
        var textNew = text
        spans.forEach { span ->
            textNew = span(textNew, span)
        }
        super.append(textNew)
        return this
    }

    fun space(): Spanly {
        super.append(" ")
        return this
    }

    fun next(): Spanly {
        super.append("\n")
        return this
    }
}