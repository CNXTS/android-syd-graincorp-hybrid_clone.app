package com.webling.graincorp.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class GrainCorpFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String TAG = "Firebase Instance ID";

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Log.d(TAG, "Refreshed token: " + newToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
}
