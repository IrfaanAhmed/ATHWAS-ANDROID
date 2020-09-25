package com.app.ia.spanly

import android.graphics.Typeface
import android.text.style.*
import android.view.View
import com.app.ia.spanly.internal.spans.CustomClickableSpan
import com.app.ia.spanly.internal.spans.FontSpan

/**
 * Created by Joshua de Guzman on 2/11/19.
 */
fun bold() = StyleSpan(Typeface.BOLD)
fun italic() = StyleSpan(Typeface.ITALIC)
fun underline() = UnderlineSpan()
fun strike() = StrikethroughSpan()
fun sup() = SuperscriptSpan()
fun sub() = SubscriptSpan()
fun background(color: Int) = BackgroundColorSpan(color)
fun color(color: Int) = ForegroundColorSpan(color)
fun size(size: Float) = RelativeSizeSpan(size)
fun font(typeface: Typeface) = FontSpan(typeface)
fun clickable(listener: View.OnClickListener, isUnderlineText: Boolean = false) = CustomClickableSpan(listener, isUnderlineText)
