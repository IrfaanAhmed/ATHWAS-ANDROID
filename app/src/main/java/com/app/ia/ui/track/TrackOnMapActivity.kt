package com.app.ia.ui.track

import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_track_on_map.*
import kotlinx.android.synthetic.main.activity_track_on_map.toolbar
import kotlinx.android.synthetic.main.common_header.view.*

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

        // Read from the database
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
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

                if (marker == null) {
                    markerList.add(LatLng(driverLat, driverLng))
                    markerObserver.value = markerList
                    marker = mMap.addMarker(MarkerOptions().position(LatLng(driverLat, driverLng)).icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.createCustomMarker(this@TrackOnMapActivity, R.drawable.ic_car_nav))))
                } else {
                    markerList[1] = LatLng(driverLat, driverLng)
                    markerObserver.value = markerList
                    MarkerAnimation.animateMarkerToGB(marker!!, LatLng(driverLat, driverLng), LatLngInterpolator.Spherical())
                }
                AppLogger.d("Value is: $driveValue")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                AppLogger.w("Failed to read value.", error.toException())
            }
        })
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