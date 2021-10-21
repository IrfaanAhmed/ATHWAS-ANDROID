package com.app.ia.ui.track

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityTrackOnMapBinding
import com.app.ia.helper.LatLngInterpolator
import com.app.ia.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.maps.android.SphericalUtil.computeHeading
import kotlinx.android.synthetic.main.activity_track_on_map.*
import kotlinx.android.synthetic.main.activity_track_on_map.toolbar
import kotlinx.android.synthetic.main.common_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker








class TrackOnMapActivity : BaseActivity<ActivityTrackOnMapBinding, TrackOnMapViewModel>(), OnMapReadyCallback {

    var mBinding: ActivityTrackOnMapBinding? = null
    private var mModel: TrackOnMapViewModel? = null

    private lateinit var ref: DatabaseReference
    private lateinit var mMap: GoogleMap

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    var storeLatitude: Double = 0.0
    var storeLongitude: Double = 0.0

    var latLngBound = LatLngBounds.Builder()

    var markerObserver: MutableLiveData<MutableList<LatLng>> = MutableLiveData()
    val markerList: MutableList<LatLng> = ArrayList()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_track_on_map
    }

    override fun getViewModel(): TrackOnMapViewModel {
        return mModel!!
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(TrackOnMapViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mModel = ViewModelProvider(this, factory).get(TrackOnMapViewModel::class.java)
    }

    var lastLatLng : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mModel?.setActivityNavigator(this)
        mModel?.setVariable(mBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.gone()
        toolbar.ivSearchIcon.gone()
        toolbar.ivEditProfileIcon.gone()

        val mapFragment = map as SupportMapFragment
        mapFragment.getMapAsync(this)

        val courierId = intent.getStringExtra("courier_id")

        storeLatitude = intent.getDoubleExtra("rest_lat", 0.0)
        storeLongitude = intent.getDoubleExtra("rest_lng", 0.0)

        ref = FirebaseDatabase.getInstance().getReference("athwas_drivers").child(courierId!!)

        AppLogger.d("UserId: $courierId")

        // Read from the database
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                AppLogger.d("Value is: ${dataSnapshot.value}")
                if(dataSnapshot.value == null)
                    return
                val driveValue = dataSnapshot.value as HashMap<*, *>
                var driverLat = 0.0
                var driverLng = 0.0

                if (driveValue["lat"] is Long) {

                    driverLat = (driveValue["lat"] as Long).toDouble()
                    driverLng = (driveValue["lng"] as Long).toDouble()

                } else if (driveValue["lat"] is Double) {

                    driverLat = driveValue["lat"] as Double
                    driverLng = driveValue["lng"] as Double
                }


                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        val client = OkHttpClient().newBuilder()
                            .build()
                        val request: Request = Request.Builder()
                            .url("https://roads.googleapis.com/v1/nearestRoads?points=$driverLat%2C$driverLng&key=AIzaSyC4MhJYysKpRFHe9Re--y5S0_PCtxGir9Q")
                            .method("GET", null)
                            .build()
                        val response: Response = client.newCall(request).execute()

                        withContext(Dispatchers.Main){
                            //Log.d("REST RESP", response.body?.string().toString())

                            val jsonData: String? = response.body?.string()
                            val Jobject = JSONObject(jsonData)
                            val points = Jobject.getJSONArray("snappedPoints")
                            if(points.length() > 0){
                                val location = points.getJSONObject(0).getJSONObject("location")
                                val lat = location.getDouble("latitude")
                                val lng = location.getDouble("longitude")

                                if (marker == null) {
                                    markerList.add(LatLng(lat, lng))
                                    markerObserver.value = markerList
                                    var heading = 0.0
                                    if(lastLatLng != null){
                                        heading = bearingBetweenLocations(lastLatLng!!, LatLng(lat, lng))
                                        //heading = computeHeading(lastLatLng, LatLng(lat, lng)).toFloat()
                                    }
                                    lastLatLng = LatLng(lat, lng)

                                    val markerOption = MarkerOptions().position(LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.createCustomMarker(this@TrackOnMapActivity, R.drawable.ic_car_marker))).anchor(0.5F,0.5F).rotation(heading.toFloat())
                                    //var icon = BitmapDescriptorFactory.fromBitmap(CommonUtils.createCustomMarker(this@TrackOnMapActivity, R.drawable.ic_car_nav))
                                    //markerOption.rotation = heading
                                    //marker.setIcon(icon);
                                    marker = mMap.addMarker(markerOption) //.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.createCustomMarker(this@TrackOnMapActivity, R.drawable.ic_car_nav))))

                                } else {
                                    markerList[1] = LatLng(lat, lng)
                                    markerObserver.value = markerList
                                    var heading = 0.0
                                    if(lastLatLng != null){
                                        heading = bearingBetweenLocations(lastLatLng!!, LatLng(lat, lng))
                                        //heading = computeHeading(lastLatLng, LatLng(lat, lng)).toFloat()
                                    }
                                    lastLatLng = LatLng(lat, lng)
                                    //rotateMarker(marker!!, heading.toFloat())
                                    MarkerAnimation.animateMarkerToGB(marker!!, LatLng(lat, lng), LatLngInterpolator.Spherical(), heading.toFloat())
                                }


                            }
                        }
                    }

                }

                /*if (marker == null) {
                    markerList.add(LatLng(driverLat, driverLng))
                    markerObserver.value = markerList
                    marker = mMap.addMarker(MarkerOptions().position(LatLng(driverLat, driverLng)).icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.createCustomMarker(this@TrackOnMapActivity, R.drawable.ic_car_nav))))
                } else {
                    markerList[1] = LatLng(driverLat, driverLng)
                    markerObserver.value = markerList
                    MarkerAnimation.animateMarkerToGB(marker!!, LatLng(driverLat, driverLng), LatLngInterpolator.Spherical())
                }*/
                AppLogger.d("Value is: $driveValue")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                AppLogger.w("Failed to read value.", error.toException())
            }
        })
    }

    var isMarkerRotating = false

    fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {
        val PI = 3.14159
        val lat1 = latLng1.latitude * PI / 180
        val long1 = latLng1.longitude * PI / 180
        val lat2 = latLng2.latitude * PI / 180
        val long2 = latLng2.longitude * PI / 180
        val dLon = long2 - long1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon))
        var brng = Math.atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        return brng
    }

    private fun rotateMarker(marker: Marker, toRotation: Float) {
        if (!isMarkerRotating) {
            val handler = Handler(Looper.myLooper()!!)
            val start: Long = SystemClock.uptimeMillis()
            val startRotation = marker.rotation
            val duration: Long = 2000
            val interpolator: Interpolator = LinearInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    isMarkerRotating = true
                    val elapsed: Long = SystemClock.uptimeMillis() - start
                    val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val rot = t * toRotation + (1 - t) * startRotation
                    val bearing = if (-rot > 180) rot / 2 else rot
                    marker.rotation = bearing
                    marker.setAnchor(0.5F,0.5F)
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    } else {
                        isMarkerRotating = false
                    }
                }
            })
        }
    }

    var marker: Marker? = null


    override fun onMapReady(googleMap: GoogleMap?) {
        this.mMap = googleMap!!
        currentLocationManager()


        val storeLocation = LatLng(storeLatitude, storeLongitude)
        markerList.add(storeLocation)

        mMap.addMarker(MarkerOptions().position(storeLocation).icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.createCustomMarker(this, R.drawable.ic_pin))))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 16.0f))

        markerObserver.observe(this, {

            if (it.size > 0) {
                for (marker in it) {
                    latLngBound.include(marker)
                }
                val bounds: LatLngBounds = latLngBound.build()
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
            }
        })
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {
        super.onCurrentLocation(latitude, longitude)
        updateLocationUI()
        /*this.latitude = latitude
        this.longitude = longitude
        val currentLocation = LatLng(latitude, longitude)*/
        /* mMap.addMarker(
             MarkerOptions().position(currentLocation).icon(
                 BitmapDescriptorFactory.fromBitmap(
                     CommonUtils.createCustomMarker(this, R.drawable.flag)
                 )
             )
         )
         mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f))*/
    }

    private fun updateLocationUI() {

        try {
            mMap.isMyLocationEnabled = false
            //mMap.uiSettings.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            AppLogger.e("Exception: %s", e.message!!)
        }
    }
}