package com.app.ia.fcm

import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.enums.NotificationType
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.home.HomeActivity
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

        val notificationCount = AppPreferencesHelper.getInstance().notificationCount
        AppPreferencesHelper.getInstance().notificationCount = (notificationCount + 1)

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            AppLogger.d("Message Notification Body: ${it.body}")
        }

        remoteMessage.data.isNotEmpty().let {

            when (remoteMessage.data["redirection"]) {

                NotificationType.NOTIFICATION_TYPE_POST_LIST.notificationType -> redirectionNotification(remoteMessage)
                NotificationType.NOTIFICATION_TYPE_ACCOUNT.notificationType -> redirectionNotification(remoteMessage)
                NotificationType.NOTIFICATION_TYPE_FOLLOWER_LIST.notificationType -> redirectionNotification(remoteMessage)
                NotificationType.NOTIFICATION_TYPE_FRIEND_LIST.notificationType -> redirectionNotification(remoteMessage)
                NotificationType.NOTIFICATION_TYPE_FRIEND_REQUEST_LIST.notificationType -> redirectionNotification(remoteMessage)
                else -> {
                    onlyNotification(remoteMessage)
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

            val redirectionId = if (remoteMessage.data["redirection_id"] != null) remoteMessage.data["redirection_id"] else ""
            val redirection = remoteMessage.data["redirection"]
            val notificationIntent = Intent(this, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(HomeActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val bundlePayloads = ArrayList<NotificationHelper.BundlePayload>()
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION, redirection!!))
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION_ID, redirectionId!!))

            val notificationHelper = NotificationHelper(this)
            notificationHelper.createNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!, HomeActivity::class.java, bundlePayloads)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun onlyNotification(remoteMessage: RemoteMessage) {

        try {

            val notificationIntent = Intent(this, HomeActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(HomeActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val notificationHelper = NotificationHelper(this)
            notificationHelper.createNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!, HomeActivity::class.java, null)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}