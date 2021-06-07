package com.app.ia.helper

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.pow

interface LatLngInterpolator {

    fun interpolate(fraction: Float, from: LatLng, to: LatLng): LatLng

    class Spherical : LatLngInterpolator {

        override fun interpolate(fraction: Float, from: LatLng, to: LatLng): LatLng {
            val fromLat: Double = toRadians(from.latitude)
            val fromLng: Double = toRadians(from.longitude)
            val toLat: Double = toRadians(to.latitude)
            val toLng: Double = toRadians(to.longitude)
            val cosFromLat: Double = kotlin.math.cos(fromLat)
            val cosToLat: Double = kotlin.math.cos(toLat)
            // Computes Spherical interpolation coefficients.
            val angle = computeAngleBetween(fromLat, fromLng, toLat, toLng)
            val sinAngle: Double = kotlin.math.sin(angle)
            if (sinAngle < 1E-6) {
                return from
            }
            val a: Double = kotlin.math.sin((1 - fraction) * angle) / sinAngle
            val b: Double = kotlin.math.sin(fraction * angle) / sinAngle
            // Converts from polar to vector and interpolate.
            val x: Double = a * cosFromLat * kotlin.math.cos(fromLng) + b * cosToLat * kotlin.math.cos(toLng)
            val y: Double = a * cosFromLat * kotlin.math.sin(fromLng) + b * cosToLat * kotlin.math.sin(toLng)
            val z: Double = a * kotlin.math.sin(fromLat) + b * kotlin.math.sin(toLat)
            // Converts interpolated vector back to polar.
            val lat: Double = kotlin.math.atan2(z, kotlin.math.sqrt(x * x + y * y))
            val lng: Double = kotlin.math.atan2(y, x)
            return LatLng(toDegrees(lat), toDegrees(lng))
        }

        private fun computeAngleBetween(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Double {
            val dLat: Double = fromLat - toLat
            val dLng = fromLng - toLng
            return 2 * kotlin.math.asin(kotlin.math.sqrt(kotlin.math.sin(dLat / 2).pow(2) + kotlin.math.cos(fromLat) * kotlin.math.cos(toLat) * kotlin.math.sin(dLng / 2).pow(2)))
        }
    }
}