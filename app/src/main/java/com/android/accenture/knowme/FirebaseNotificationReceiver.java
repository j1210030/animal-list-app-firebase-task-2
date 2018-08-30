package com.android.accenture.knowme;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;
import static com.android.accenture.knowme.Constant.CHECK_CONTAINS_NOTIFICATION;
import static com.android.accenture.knowme.Constant.CHECK_CONTAINS_PAYLOAD;
import static com.android.accenture.knowme.Constant.FROM_LOG;
import static com.android.accenture.knowme.Constant.MESSAGE_CONST;

/**
 * Created by ykashiwagi on 7/12/17.
 */

public class FirebaseNotificationReceiver extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)  {

        // Popup Dialog
        Intent i = new Intent(getApplicationContext(),CallDialogActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(MESSAGE_CONST, remoteMessage.getNotification().getBody());
        getApplicationContext().startActivity(i);

        Log.d(TAG, FROM_LOG + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, CHECK_CONTAINS_PAYLOAD + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, CHECK_CONTAINS_NOTIFICATION + remoteMessage.getNotification().getBody());
        }
    }
}
