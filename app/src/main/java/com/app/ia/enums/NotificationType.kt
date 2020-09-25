package com.app.ia.enums

enum class NotificationType(var notificationType: String) {

    NOTIFICATION_TYPE_POST_LIST("post_list"),
    NOTIFICATION_TYPE_FOLLOWER_LIST("followers_list"),
    NOTIFICATION_TYPE_FRIEND_LIST("friends_list"),
    NOTIFICATION_TYPE_FRIEND_REQUEST_LIST("friend_requests_list"),
    NOTIFICATION_TYPE_ACCOUNT("account")

}