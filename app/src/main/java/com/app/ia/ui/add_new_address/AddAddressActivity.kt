package com.app.ia.ui.add_new_address

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityAddAddressBinding
import com.app.ia.utils.AppLogger
import com.example.easywaylocation.LocationData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_add_address.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AddAddressActivity : BaseActivity<ActivityAddAddressBinding, AddAddressViewModel>(), OnMapReadyCallback {

    private var mActivityAddressBinding: ActivityAddAddressBinding? = null
    private var mAddAddressViewModel: AddAddressViewModel? = null

    //private lateinit var ref: DatabaseReference
    private lateinit var mMap: GoogleMap

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var mPlacesAutoCompleteAdapter: PlacesAutoCompleteAdapter

    val subject = PublishSubject.create<String>()
    private var disposable: Disposable? = null

    private var selectedFromAddressBar = false

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_address
    }

    override fun getViewModel(): AddAddressViewModel {
        return mAddAddressViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityAddressBinding = getViewDataBinding()
        mActivityAddressBinding?.lifecycleOwner = this
        mAddAddressViewModel?.setActivityNavigator(this)
        mAddAddressViewModel?.setVariable(mActivityAddressBinding!!)

        setSupportActionBar(toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar

        // Set action bar elevation
        actionBar!!.elevation = 10.0F

        val mapFragment = map as SupportMapFragment

        mapFragment.getMapAsync(this)

        mPlacesAutoCompleteAdapter = PlacesAutoCompleteAdapter(this, R.layout.layout_autocomlete_list_item)
        edtTextSearch.setAdapter(mPlacesAutoCompleteAdapter)
        edtTextSearch.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            selectedFromAddressBar = true
            mAddAddressViewModel!!.currentAddress.set(resultList[position]["address"])
            val placeId: String = resultList[position]["place_id"]!!
            // Specify the fields to return.
            val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Construct a request object, passing the place ID and fields array.
            if (!Places.isInitialized()) {
                Places.initialize(this, getString(R.string.google_map_direction_key), Locale.US)
            }

            val placesClient = Places.createClient(this)
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            placesClient.fetchPlace(request).addOnSuccessListener {
                val place = it.place
                latitude = place.latLng?.latitude!!
                longitude = place.latLng?.longitude!!
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 16.0f))
            }
        }

        edtTextAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mAddAddressViewModel?.enteredAddress!!.set(p0.toString())
            }

        })

        observeRestaurantListResponse()

        disposable = subject.debounce(300, TimeUnit.MILLISECONDS).filter { text ->
                text.isNotEmpty()
            }.distinctUntilChanged().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                mAddAddressViewModel?.getAddress(it)
            }

        imgViewCross.setOnClickListener {
            mAddAddressViewModel?.searchedLocationName!!.set("")
            edtTextSearch.setText("")
        }

        //makeStatusBarTransparent()
        //setOnApplyWindowInset(toolbar, content_container)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(AddAddressViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mAddAddressViewModel = ViewModelProvider(this, factory).get(AddAddressViewModel::class.java)
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        this.mMap = googleMap!!

        currentLocationManager()

        mMap.setOnCameraIdleListener {
            mMap.projection.visibleRegion.latLngBounds.center.let {
                if (!selectedFromAddressBar) {
                    latitude = it.latitude
                    longitude = it.longitude
                    mAddAddressViewModel?.cancelApi()
                    //mAddAddressViewModel?.getAddressFromLatLng(latitude, longitude)
                    mLocationManager?.getLocationAddress(latitude, longitude)
                    AppLogger.w("Latitude is : $latitude Longitude is$longitude")
                } else {
                    selectedFromAddressBar = false
                }
            }
        }
    }

    private fun observeRestaurantListResponse() {
        mAddAddressViewModel?.resultList!!.observe(this, {
            resultList.clear()
            resultList.addAll(it)
            setAdapter()
        })
    }

    private fun setAdapter() {
        mPlacesAutoCompleteAdapter.notifyDataSetChanged()
    }

    val resultList: MutableList<MutableMap<String, String>> = ArrayList()

    inner class PlacesAutoCompleteAdapter(mContext: Context, mResource: Int) : ArrayAdapter<String>(mContext, mResource), Filterable {

        override fun getCount(): Int {
            var size = 0
            try {
                size = resultList.size
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Last item will be the footer
            return size
        }

        override fun getItem(position: Int): String? {
            return resultList[position]["address"]
        }

        override fun getFilter(): Filter {
            var filter: Filter? = null
            try {
                filter = object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val filterResults = FilterResults()

                        if (constraint != null) {
                            mAddAddressViewModel!!.searchedLocationName.set(constraint.toString())
                            subject.onNext(constraint.toString())

                            filterResults.values = resultList
                            filterResults.count = resultList.size
                        }
                        return filterResults
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return filter!!
        }
    }

    override fun onBackPressed() {
        disposable?.dispose()
        mLocationManager?.removeLocationUpdate()
        if (mAddAddressViewModel?.isAddressAdded!!) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {
        super.onCurrentLocation(latitude, longitude)
        updateLocationUI()
        this.latitude = latitude
        this.longitude = longitude
        val currentLocation = LatLng(latitude, longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f))
    }


    private fun updateLocationUI() {

        try {
            mMap.isMyLocationEnabled = true
            //mMap.uiSettings.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            AppLogger.e("Exception: %s", e.message!!)
        }
    }

    override fun onAddressUpdate(locationData: LocationData?) {
        super.onAddressUpdate(locationData)
        mAddAddressViewModel?.currentAddress?.set(locationData?.full_address)
    }
}