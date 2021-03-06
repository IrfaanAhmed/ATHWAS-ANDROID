package com.app.ia.spanly.internal.spans

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * Created by Joshua de Guzman on 2/12/19.
 */
class CustomClickableSpan(private val listener: View.OnClickListener, private val isUnderlineText: Boolean) : ClickableSpan() {

    override fun onClick(widget: View) {
        listener.onClick(widget)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderlineText
    }
}