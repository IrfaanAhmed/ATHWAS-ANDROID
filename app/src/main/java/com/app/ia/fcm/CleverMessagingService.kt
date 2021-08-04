package com.app.ia.fcm

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.IAApplication
import com.app.ia.enums.NotificationType
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.order_detail.OrderDetailActivity
import com.app.ia.ui.track_order.TrackOrderActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.AppLogger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import java.util.*


class CleverMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        AppLogger.d("From : ${remoteMessage.from}")
        AppLogger.d("Data : ${remoteMessage.data}")
        AppLogger.d("Msg : ${remoteMessage.notification}")

        val notificationCount = AppPreferencesHelper.getInstance().notificationCount
        AppPreferencesHelper.getInstance().notificationCount = (notificationCount + 1)

        val localBroadCast = LocalBroadcastManager.getInstance(this)
        val intent = Intent(AppConstants.ACTION_BROADCAST_REFRESH_ON_NOTIFICATION)
        intent.putExtra("refresh", true)
        localBroadCast.sendBroadcast(intent)

        val currentActivity = (applicationContext as IAApplication).getCurrentActivity()

        val handler = Handler(Looper.getMainLooper())

        handler.post {
            if (currentActivity != null && currentActivity is OrderDetailActivity) {
                currentActivity.refresh()
            } else if (currentActivity != null && currentActivity is TrackOrderActivity) {
                currentActivity.refresh()
            }
        }

        // Check if message contains a notification payload.
        /*remoteMessage.notification?.let {
            AppLogger.d("Message Notification Body: ${it.body}")
        }*/

        if(AppPreferencesHelper.getInstance().allowNotification == 1) {
            remoteMessage.data.isNotEmpty().let {

                when (remoteMessage.data["custom_message_type"]) {

                    NotificationType.NOTIFICATION_TYPE_ADMIN.notificationType -> redirectionNotification(remoteMessage)
                    NotificationType.NOTIFICATION_TYPE_ORDER_DETAIL.notificationType -> redirectionNotification(remoteMessage)
                    NotificationType.NOTIFICATION_TYPE_PRODUCT.notificationType -> redirectionNotification(remoteMessage)
                    NotificationType.NOTIFICATION_TYPE_ORDER_LIST.notificationType -> redirectionNotification(remoteMessage)
                    else -> {
                        onlyNotification(remoteMessage)
                    }
                }
            }
        }
        // Also, if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        AppLogger.d("Refreshed token: $token")
        AppPreferencesHelper.getInstance().deviceToken = token
    }
    // [END on_new_token]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private fun redirectionNotification(remoteMessage: RemoteMessage) {

        try {

            val redirectionId = if (remoteMessage.data["id"] != null) remoteMessage.data["id"] else ""
            val redirection = remoteMessage.data["custom_message_type"]
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            val notificationIntent = Intent(this, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(HomeActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val bundlePayloads = ArrayList<NotificationHelper.BundlePayload>()
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION, redirection!!))
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION_ID, redirectionId!!))

            val notificationHelper = NotificationHelper(this)
            notificationHelper.createNotification(title!!, body!!, HomeActivity::class.java, bundlePayloads)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun onlyNotification(remoteMessage: RemoteMessage) {

        try {

            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            val notificationIntent = Intent(this, HomeActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(HomeActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val notificationHelper = NotificationHelper(this)
            notificationHelper.createNotification(title!!, body!!, HomeActivity::class.java, null)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}