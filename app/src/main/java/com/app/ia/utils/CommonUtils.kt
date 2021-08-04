package com.app.ia.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.app.ia.IAApplication
import com.app.ia.R
import com.app.ia.local.AppPreferencesHelper
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object CommonUtils {

    val timestamp: String
        get() = SimpleDateFormat(AppConstants.TIME_STAMP_FORMAT, Locale.US).format(Date())

    val chatTimeStamp: String
        get() = SimpleDateFormat(AppConstants.CHAT_TIME_STAMP_FORMAT, Locale.US).format(Date())

    @SuppressLint("all")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateNumber(countryCode: String, phNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode))
        var phoneNumber: Phonenumber.PhoneNumber? = null
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        return phoneNumberUtil.isValidNumber(phoneNumber)
    }

    /**
     * Show the soft input.
     *
     * @param activity The activity.
     */
    fun showSoftInput(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    fun hideSoftInput(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showCartItemCount(countTextView: AppCompatTextView) {
        val itemCount = AppPreferencesHelper.getInstance().cartItemCount
        if (itemCount <= 0) {
            countTextView.visibility = View.INVISIBLE
        } else {
            countTextView.visibility = View.VISIBLE
            countTextView.text = itemCount.toString()
        }
    }

    fun showNotificationCount(countTextView: AppCompatTextView) {
        val notificationCount = AppPreferencesHelper.getInstance().notificationCount
        if (notificationCount <= 0) {
            countTextView.visibility = View.INVISIBLE
        } else {
            countTextView.visibility = View.VISIBLE
            countTextView.text = notificationCount.toString()
        }
    }

    /**
     * Hide the soft input.
     */
    fun hideSoftInput(view: View) {
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive)
            imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("PackageManagerGetSignatures", "WrongConstant")
    fun hashKey(context: Context): String {
        var keyhash = ""
        try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo.apkContentsSigners
            } else {
                info.signatures
            }

            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                AppLogger.d(Base64.encodeToString(md.digest(), Base64.DEFAULT))
                keyhash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
        return keyhash
    }

    /*
     * Method to convert number into 2 points after decimal.
     * */
    fun convertToDecimal(value: Any?): String {
        if (value == null) {
            return "0"
        }

        val symbols = DecimalFormatSymbols(Locale.ENGLISH)
        val df2 = DecimalFormat("#0.##", symbols)
        return when (value) {
            is Float -> df2.format(value)
            is Double -> df2.format(value)
            is String -> df2.format(value.toDouble())
            is Int -> df2.format((value.toString()).toDouble())
            else -> "0"
        }
    }

    /*
     * Method to convert number into 2 points after decimal.
     * */
    fun convertToKm(value: Any): String {
        val symbols = DecimalFormatSymbols(Locale.ENGLISH)
        val df2 = DecimalFormat("#0.0#", symbols)
        return when (value) {
            is Float -> String.format("%.1f", df2.format(value).toDouble())
            is Double -> df2.format(value)
            is String -> df2.format(value.toDouble())
            is Int -> df2.format((value.toString()).toDouble())
            else -> "0"
        }
    }

    /*
     * Method to convert ratings into 1 points after decimal.
     * */
    fun convertToDecimalRating(value: Any): String {
        val symbols = DecimalFormatSymbols(Locale.ENGLISH)
        val df2 = DecimalFormat("0.0", symbols)
        return when (value) {
            is Float -> df2.format(value)
            is Double -> df2.format(value)
            is String -> df2.format(value.toDouble())
            is Int -> df2.format((value.toString()).toDouble())
            else -> "0.0"
        }
    }

    /*
     * Method to convert Currency format.
     * */
    fun changeCurrencyFormat(value: Any): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.minimumFractionDigits = 2
        format.currency = Currency.getInstance(Locale("es", "US"))
        return when (value) {
            is Float -> format.format(value).replace("US", "DOP ")
            is Double -> format.format(value).replace("US", "DOP ")
            is String -> format.format(value.toDouble()).replace("US", "DOP ")
            is Int -> format.format((value.toString()).toDouble()).replace("US", "DOP ")
            else -> format.format(0).replace("US", "DOP ")
        }
    }

    fun setDrawable(view: TextView?) {
        if (view != null) {
            if (AppPreferencesHelper.getInstance().language == "en") {
                val wrappedDrawable = DrawableCompat.wrap(view.compoundDrawables[0])
                if (wrappedDrawable != null) {
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(IAApplication.getInstance(), R.color.light_grey))
                }
            } else if (AppPreferencesHelper.getInstance().language == "ar") {
                var wrappedDrawable = DrawableCompat.wrap(view.compoundDrawables[0])
                if (wrappedDrawable != null) {
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(IAApplication.getInstance(), R.color.light_grey))
                } else {
                    wrappedDrawable = DrawableCompat.wrap(view.compoundDrawables[2])
                    if (wrappedDrawable != null) {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(IAApplication.getInstance(), R.color.light_grey))
                    }
                }
            }
        }
    }

    /*
    * Slide Up a View with animation
    * */
    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            view.height.toFloat(), // fromYDelta
            0f  // toYDelta
        )
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    /*
    *
    * Slide down a view with animation
    * */
    fun slideDown(view: View) {
        val animation = TranslateAnimation(0f, 0f, 0f, view.height.toFloat())
        animation.duration = 500
        animation.fillAfter = true
        view.startAnimation(animation)
        view.visibility = View.GONE
    }

    /*
    * Decode Base64 String
    * */
    fun decodeBase64(base64: String): String {
        return Base64.decode(base64, Base64.DEFAULT).toString(charset("UTF-8"))
    }

    fun isBase64(base64String: String): Boolean {
        if (base64String.isEmpty() || base64String.length % 4 != 0 || base64String.contains(" ") || base64String.contains("\t") || base64String.contains("\r") || base64String.contains("\n")) {
            return false
        } else {
            try {
                return true
            } catch (e: Exception) {
            }
        }
        return false
    }

    /*
    * Get Current Date
    * */
    fun getCurrentDate(): String {
        val date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return sdf.format(date)
    }

    /*
    * Create Custom marker from Drawable
    * */
    fun createCustomMarker(context: Context, resource: Int): Bitmap {
        val marker: View = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_simple_marker_layout, null)
        val ivMarker: AppCompatImageView = marker.findViewById(R.id.ivMarker) as AppCompatImageView
        ivMarker.setImageResource(resource)
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(70, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(marker.measuredWidth, marker.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }

    /*
    * Open Google Map with latitude longitude
    * */
    fun openGoogleMapWithLocation(context: Context, currentLat: String, currentLong: String, desLat: String, desLong: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr=$currentLat,$currentLong&daddr=$desLat,$desLong&hl=zh&t=m&dirflg=d"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
        context.startActivity(intent)
    }

    /*
    * Calculate Distance between two location point
    * */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lon1

        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lon2

        val distanceInMeters = loc1.distanceTo(loc2)
        return distanceInMeters / 1000
    }

    /*
    * Create Multipart Image File Object
    * */
    fun prepareFilePart(context: Context, partName: String, fileUri: Uri, imageFile: File): MultipartBody.Part {
        val extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
        val requestFile = imageFile.asRequestBody("image/$extension".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, imageFile.name, requestFile)
    }

    /*
    * Create Part Data From String
    * */
    fun prepareDataPart(data: String): RequestBody {
        return data.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    /*
    * Get Bitmap from File Path
    * */
    fun getBitmap(path: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            //image.setImageBitmap(bitmap)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return bitmap
    }


    /*
     * Date Picker and set on Edit text
     * */
    fun openDatePicker(context: Context, editText: AppCompatEditText) {
        val dpd = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val myCalendar = Calendar.getInstance()
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            editText.setText(sdf.format(myCalendar.time))
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val d = DatePickerDialog(context, dpd, year, month, day)
        /*if (editText.id == R.id.editTextStartDate || editText.id == R.id.editTextEndDate) {
            d.datePicker.minDate = System.currentTimeMillis()
        }*//* else if (editText.id == R.id.edtDateCreated) {
             d.datePicker.maxDate = System.currentTimeMillis()
         } else if (editText.id == R.id.edtLastUpdated) {
             d.datePicker.maxDate = System.currentTimeMillis()
         }*/
        d.show()
    }

    /*
    * Change Date Format
    * */
    fun toDateTime(
        value: String?,
        currFormat: String,
        timeFormat: String
    ): String {
        if (value != null) {
            AppLogger.d(value)
        }
        if (value.isNullOrEmpty())
            return "N/A"
        else {
            try {
                val inputFormat = SimpleDateFormat(currFormat)
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                val date = inputFormat.parse(value)
                val formatted = SimpleDateFormat(timeFormat)
                formatted.setTimeZone(TimeZone.getDefault());
                val formattedDate = formatted.format(date)
                println(formattedDate) // prints 10-04-2018
                AppLogger.d(" Date : $value")
                AppLogger.d("formatted Date : ${formattedDate}")
                return formattedDate
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }
    }

    /*
    * Get Address from Location
    * */
    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {

        val geoCoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        val sb = StringBuilder()

        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName

            sb.append(address).append("")
        }
        return sb.toString()
    }

    fun getEditTextFilter(): InputFilter? {
        return object : InputFilter {
            override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
                var keepOriginal = true
                val sb = StringBuilder(end - start)
                for (i in start until end) {
                    val c = source[i]
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c) else keepOriginal = false
                }
                return if (keepOriginal) null else {
                    if (source is Spanned) {
                        val sp = SpannableString(sb)
                        TextUtils.copySpansFrom(source, start, sb.length, null, sp, 0)
                        sp
                    } else {
                        sb
                    }
                }
            }

            private fun isCharAllowed(c: Char): Boolean {
                val ps: Pattern = Pattern.compile("^[a-zA-Z0-9-_@. ]+$")
                val ms: Matcher = ps.matcher(c.toString())
                return ms.matches()
            }
        }
    }

    fun getLettersEditTextFilter(): InputFilter? {
        return object : InputFilter {
            override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
                var keepOriginal = true
                val sb = java.lang.StringBuilder(end - start)
                for (i in start until end) {
                    val c = source[i]
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c) else keepOriginal = false
                }
                return if (keepOriginal) null else {
                    if (source is Spanned) {
                        val sp = SpannableString(sb)
                        TextUtils.copySpansFrom(source, start, sb.length, null, sp, 0)
                        sp
                    } else {
                        sb
                    }
                }
            }

            private fun isCharAllowed(c: Char): Boolean {
                val ps = Pattern.compile("^[a-zA-Z ]+$")
                val ms = ps.matcher(c.toString())
                return ms.matches()
            }
        }
    }

    /*fun shareApp(mContext: Context) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(Api.BASE_URL))
            .setDomainUriPrefix(AppConstants.DEEP_LINK_DOMAIN_PREFIX)
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            .setIosParameters(DynamicLink.IosParameters.Builder(AppConstants.IOS_PACKAGE_NAME)
                .setAppStoreId(AppConstants.IOS_APP_STORE_ID)
                .build())
            // Set parameters
            .buildShortDynamicLink()
            .addOnSuccessListener { result ->
                // Short link created
                val shortLink = result.shortLink
                val sendIntent = Intent()
                val msg = "I request you to pay some money via this link:- \n $shortLink"
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                sendIntent.type = "text/plain"
                mContext.startActivity(sendIntent)
            }.addOnFailureListener {
                it.printStackTrace()
            }
    }*/
}