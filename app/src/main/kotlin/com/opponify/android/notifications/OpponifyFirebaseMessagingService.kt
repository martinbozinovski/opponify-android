package com.opponify.android.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class OpponifyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        // Domain truth remains in the backend/in-app notification history.
        // This service is intentionally only a delivery signal boundary.
    }

    override fun onNewToken(token: String) {
        // Token registration is deferred to the authenticated notification
        // repository; the token itself is never treated as domain truth.
    }
}
