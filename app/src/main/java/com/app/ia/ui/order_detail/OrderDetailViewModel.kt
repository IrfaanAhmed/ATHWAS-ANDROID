package com.app.ia.ui.order_detail

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityOrderDetailBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.OrderDetailResponse
import com.app.ia.ui.track_order.TrackOrderActivity
import com.app.ia.utils.AppLogger
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.Resource
import com.app.ia.utils.mStartActivityForResult
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.Dispatchers
import java.io.File

class OrderDetailViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {


    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOrderDetailBinding
    val order_id = MutableLiveData("")

    var orderDetailResponse = MutableLiveData<OrderDetailResponse>()

    fun getOrderDetail(): MutableLiveData<OrderDetailResponse> {
        return orderDetailResponse
    }

    fun setVariable(mBinding: ActivityOrderDetailBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("Order Detail")
        order_id.value = intent.getStringExtra("order_id")!!
        //orderDetailObserver(order_id.value!!)
    }

    fun onDownloadInvoice() {

        Dexter.withContext(mActivity).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    val params = HashMap<String, String>()
                    params["order_id"] = order_id.value!!
                    downloadInvoiceObserver(params)
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                token.continuePermissionRequest()
            }
        }).check()

    }

    fun onOrderTrackClick() {
        mActivity.mStartActivityForResult<TrackOrderActivity>(AppRequestCode.REQUEST_ORDER_STATUS) {
            putExtra("order_id", orderDetailResponse.value?.Id!!)
            putExtra("orderStatus", orderDetailResponse.value?.currentTrackingStatus)
        }
    }

    private fun orderDetail(requestParams: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.orderDetail(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun orderDetailObserver(requestParams: String) {

        orderDetail(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            orderDetailResponse.value = users.data!!
                            Handler(Looper.myLooper()!!).postDelayed({
                                (mActivity as OrderDetailActivity).onReturnClicked = false
                            }, 5000)
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }


    /**
     *
     */
    private fun cancelReturnOrder(requestParams: HashMap<String, String>, isGroceryOrder: Boolean, isReturn: Boolean) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            if (isReturn) {
                if (isGroceryOrder) {
                    emit(Resource.success(data = baseRepository.returnGroceryOrder(requestParams)))
                } else {
                    emit(Resource.success(data = baseRepository.returnOrder(requestParams)))
                }
            } else {
                if (isGroceryOrder) {
                    emit(Resource.success(data = baseRepository.cancelGroceryOrder(requestParams)))
                } else {
                    emit(Resource.success(data = baseRepository.cancelOrder(requestParams)))
                }
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun cancelReturnOrderObserver(requestParams: HashMap<String, String>, isGroceryOrder: Boolean, isReturn: Boolean) {

        cancelReturnOrder(requestParams, isGroceryOrder, isReturn).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let {
                            orderDetailObserver(order_id.value!!)
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }


    /**
     * Download Invoice Option
     */
    private fun downloadInvoice(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.downloadInvoice(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun downloadInvoiceObserver(requestParams: HashMap<String, String>) {

        downloadInvoice(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            invoicePath.value = users.data?.path
                            downloadImage()
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private var invoicePath = MutableLiveData<String>()
    private var dirPath: File? = null
    private var fileName: String? = null
    private var downloadID: Long = 0L

    @Suppress("DEPRECATION")
    fun downloadImage() {
        baseRepository.callback.showProgress()
        if (invoicePath.value!!.isNotEmpty()) {
            val dirName = "Athwas"
            fileName = invoicePath.value?.substring(invoicePath.value!!.lastIndexOf('/').plus(1), invoicePath.value!!.length)

            //Folder Creating Into Phone Storage
            dirPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/" + dirName + "/")

            if (!dirPath!!.exists()) {
                dirPath!!.mkdir()
                AppLogger.d("dir created for first time")
            }

            val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            getActivityNavigator()?.registerReceiver(mDownloadReceiver, intentFilter)

            val dm = getActivityNavigator()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(invoicePath.value)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setMimeType("application/pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + dirName + File.separator.toString() + fileName)

            downloadID = dm.enqueue(request)
        } else {
            baseRepository.callback.hideProgress()
            IADialog(mActivity, mActivity.getString(R.string.no_order_invoice_available), true)
        }
    }

    private val mDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            baseRepository.callback.hideProgress()
            //Fetching the download id received with the broadcast
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                IADialog(mActivity, mActivity.getString(R.string.invoice_downloaded_successfully), true)
                getActivityNavigator()?.unregisterReceiver(this)
            }
        }
    }
}