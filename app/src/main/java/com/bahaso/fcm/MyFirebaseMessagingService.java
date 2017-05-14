/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bahaso.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.bahaso.R;
//import com.bahaso.activity.WritingDetailNewActivity;
//import com.bahaso.application.MyApplication;
//import com.bahaso.helper.PreferencesHelper;
//import com.bahaso.model.BahasoNotification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    // private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. BahasoNotification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("message"));
//            remoteMessage.getData().get("type");
//            remoteMessage.getData().get("writing_id");
             sendNotification(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message BahasoNotification Body: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getBody());
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @ param messageBody FCM message body received.
     */
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, FrontPageActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.mascot)
//                .setContentTitle("FCM Message")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }

    private void sendNotification(Map<String,String> mapData) {

        if (mapData.containsKey(BahasoNotificationFCM.TYPE) ) {
            String type = mapData.get(BahasoNotificationFCM.TYPE);
            if (type.equals(BahasoNotificationFCM.TYPE_REVIEW)) {
//                if (MyApplication.activityCount == 0) { // no visible activity
                    // launch notification
                    // REVIEW
//                    Intent writingDetailIntent = new Intent(this, WritingDetailNewActivity.class);
//                    writingDetailIntent.putExtra(WritingDetailNewActivity.WRITINGHIST_ID,
//                            mapData.get(BahasoNotificationFCM.WRITING_ID));
//                    writingDetailIntent.putExtra(WritingDetailNewActivity.NEEDLOADSERVER,true);

                    // use this if want to destroy and recreate the new activity
                    // http://stackoverflow.com/questions/13148701/taskstackbuilder-and-extras-for-the-back-stack
                    //Intent homeActivityIntent = new Intent(this, com.bahaso.activity.HomeOldActivity.class);
                    //homeActivityIntent.putExtra(HomeOldActivity.DISPLAY, HomeOldActivity.MY_WRITING);
                    //homeActivityIntent.putExtra(HomeOldActivity.NAVITEM_POSITION, HomeOldActivity.WRITING_POS_IN_LIST);
                    //
                    //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    //stackBuilder.addNextIntent(homeActivityIntent);
                    //stackBuilder.addNextIntent(writingDetailIntent);
                    //
                    //PendingIntent pendingIntent =
                    //        stackBuilder.getPendingIntent(
                    //                0,
                    //                PendingIntent.FLAG_ONE_SHOT
                    //        );

//                    PendingIntent pendingIntent =
//                            PendingIntent.getActivity(this, 0, writingDetailIntent,
//                                    PendingIntent.FLAG_ONE_SHOT);
//
//                    // to refresh writing
//                    PreferencesHelper.setWritingTimeStamp(false);
//
//                    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//                    NotificationCompat.Builder notificationBuilder =
//                            new NotificationCompat.Builder(this)
//                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) )
//                                    .setSmallIcon(R.drawable.mascot)
//                                    .setColor(ContextCompat.getColor(this, R.color.bahaso_blue))
//                                    .setContentTitle("Bahaso")
//                                    .setContentText(mapData.get(BahasoNotification.MESSAGE))
//                                    .setAutoCancel(true)
//                                    .setSound(defaultSoundUri)
//                                    .setContentIntent(pendingIntent);
//
//                    NotificationManager notificationManager =
//                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                    Notification notification = notificationBuilder.build();
//
//                    notificationManager.notify(1 /* ID of notification */, notification);
//                }
//            }
//            else if (type.equals(BahasoNotification.TYPE_GOLD)) {
//                // GOLD_PAGE
//                int gold;
//                try {
//                    gold = Integer.parseInt(mapData.get(BahasoNotification.MESSAGE));
//                }
//                catch (Exception e) {
//                    gold = -1;
//                }
//                if (gold < 0) return;
//
//                Intent successIntent = new Intent(PurchaseService.ACTION_GETGOLD);
//                successIntent.putExtra(PurchaseService.EXTRA_GOLD, gold);
//                successIntent.putExtra(PurchaseService.EXTRA_RECEIVE_STATUS,
//                        BahasoNetworkService.STAT_SUCCESS);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);

            }

        }


    }

}
