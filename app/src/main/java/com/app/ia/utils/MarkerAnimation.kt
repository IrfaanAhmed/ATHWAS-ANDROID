package com.app.ia.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import com.app.ia.helper.LatLngInterpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

object MarkerAnimation {

    fun animateMarkerToGB(marker: Marker, finalPosition: LatLng, latLngInterpolator: LatLngInterpolator, rotation: Float) {
        marker.rotation = rotation
        val startPosition = marker.position
        val start = SystemClock.uptimeMillis()
        val handler = Handler(Looper.myLooper()!!)
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 2000f

        handler.post(object : Runnable {

            var elapsed = 0L
            var t = 0.0f
            var v = 0.0f

            override fun run() {

                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                marker.position = latLngInterpolator.interpolate(v, startPosition, finalPosition)
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }
}