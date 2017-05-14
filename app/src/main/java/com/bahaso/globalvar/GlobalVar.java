package com.bahaso.globalvar;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bahaso.R;
import com.bahaso.fcm.BahasoNotificationFCM;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.api.models.consumer.push.PNPushRemoveChannelResult;
import com.securepreferences.SecurePreferences;
import com.squareup.leakcanary.RefWatcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bahaso.BuildConfig.PRODUCTION;


public class GlobalVar extends MultiDexApplication {

    private static GlobalVar mInstance;
    private String baseURL, baseURLpath;
    private SecurePreferences mSecurePrefs;
    private SharedPreferences sharedpref;
    private static final String MyPREFERENCES = "myprefs.xml";
    private int APIversion;
    private static File mAvatarFile = null;
    private static String VT_CLIENT_KEY_PROD, VT_CLIENT_KEY_DEV;
    private static String PUBNUB_CLIENT_KEY;
    private PubNub mPubNub;
    private Tracker Tracker;
    private RefWatcher refWatcher;

    public void onCreate(){
        super.onCreate();
        mInstance = this;

        setLocale("id");

        /***** development *****/
//        baseURL = "http://dc33c8b0a252f4219c9698652d4e4708.bahaso.com/";
        /***** devQA *****/
//        baseURL = "http://ui.bahaso.com/";

        /***** productionTesting *****/
//        baseURL = "https://procopy.bahaso.com/";
        /***** productionFullRelease *****/
//        baseURL = "https://bahaso.com/"; //

        baseURL = GlobalVar.getContext().getString(R.string.PROTOCOL) + "://" + GlobalVar.getContext().getString(R.string.HOST) + "/";
        Log.i("baseURL tes1", baseURL);

        baseURLpath =  baseURL + "api/v2/";
        Log.i("baseURLpath tes1", baseURLpath);

        APIversion = Build.VERSION.SDK_INT;
//        analyticsPropertyID = "UA-92480491-1";
        VT_CLIENT_KEY_PROD = "VT-client-WYpV4CtAxuv9wVva";
        VT_CLIENT_KEY_DEV = "VT-client-4idVtKT26SiMnX9O";
        PUBNUB_CLIENT_KEY = "sub-c-dd86a910-a56d-11e6-80e1-0619f8945a4f";

    }

    public static synchronized GlobalVar getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        try {
            return mInstance.getApplicationContext();
        } catch (Exception e) {
            return null;
        }
    }

    public String getBaseURL() { return baseURL; }

    public String getBaseURLpath () { return baseURLpath; }

    public int getAPIversion(){ return APIversion; }

    public String getVT_CLIENT_KEY_PROD() { return VT_CLIENT_KEY_PROD; }

    public String getVT_CLIENT_KEY_DEV() { return VT_CLIENT_KEY_DEV; }


    public static RefWatcher getRefWatcher(Context context) {
        GlobalVar application = (GlobalVar) context.getApplicationContext();
        return application.refWatcher;
    }

    public SharedPreferences getSharedPreferences() {
        if(mSecurePrefs==null){
            mSecurePrefs = new SecurePreferences(this, "q1w2E#R$T%Y^U&I*O(P)", MyPREFERENCES);
            SecurePreferences.setLoggingEnabled(true);
        }
        return mSecurePrefs;
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        //noinspection deprecation
        conf.locale = myLocale;
        //noinspection deprecation
        res.updateConfiguration(conf, dm);
    }

    public static File getAvatarFile(Context context){
        if (null == mAvatarFile) {
            File avatarFolder = new File(context.getFilesDir(), "avatar/");
            if (!avatarFolder.exists()) avatarFolder.mkdirs();
            mAvatarFile = new File(avatarFolder, "avatar.jpg");
        }
        return mAvatarFile;
    }

    public static File bitmapToAvatarFile(Context context, Bitmap bitmap) throws IOException {
        Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), true);

        File imageFile = getAvatarFile(context);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        imageFile.createNewFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        bitmapCopy.recycle();

        return imageFile;
    }

    public static void subscribePubNub(){
        GlobalVar myApplication= getInstance();
        PubNub pubNub = myApplication.getPubNub();

        List<String> channelList = new ArrayList<>();
        String bahasoChannel = BahasoNotificationFCM.getBahasoChannel();
        channelList.add(bahasoChannel);
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" +bahasoChannel);
//        User user = PreferencesHelper.getUser();
//        if (null!= user && !TextUtils.isEmpty( user.getId()))
        if(null != getInstance().getSharedPref().getString(getInstance().getResources().getString(R.string.userid),"")
                && !TextUtils.isEmpty(getInstance().getSharedPref().getString(getInstance().getResources().getString(R.string.userid),""))){
            String userChannel = BahasoNotificationFCM.getUserChannel(getInstance().getSharedPref()
                    .getString(getInstance().getResources().getString(R.string.userid),""));
            channelList.add(userChannel);
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + userChannel);

            getInstance().getSharedPref().edit().putBoolean(getInstance().getResources()
                    .getString(R.string.flag_pubnub_token_login), true).apply();
        }

        pubNub.subscribe().channels(channelList).execute();
        pubNub.addPushNotificationsOnChannels()
                .pushType(PNPushType.GCM)
                .channels(channelList)
                .deviceId(FirebaseInstanceId.getInstance().getToken())
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result,
                                           PNStatus status) {
                        // handle response.
                        Log.i("test", "success subscribe");
                    }
                });
    }

    public static void unSubscribePubNub(String channel){
        GlobalVar myApplication= getInstance();
        PubNub pubNub = myApplication.getPubNub();

        List<String> channelList = new ArrayList<>();
        channelList.add(channel);
        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/" + channel);

        pubNub.removePushNotificationsFromChannels()
                .deviceId(FirebaseInstanceId.getInstance().getToken())
                .pushType(PNPushType.GCM)
                .channels(channelList)
                .async(new PNCallback<PNPushRemoveChannelResult>() {
                    @Override
                    public void onResponse(PNPushRemoveChannelResult result, PNStatus status) {
                        Log.i("test", "success unsubsrcibe");
                    }
                });

        pubNub.unsubscribe()
                .channels(channelList)
                .execute();
    }


    public PubNub getPubNub (){

        if (null == mPubNub) {
            // PUBNUB
            PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setSubscribeKey(PUBNUB_CLIENT_KEY);

            mPubNub = new PubNub(pnConfiguration);

            mPubNub.addListener(new SubscribeCallback() {
                @Override
                public void status(PubNub pubnub, PNStatus status) {

//                    if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
//                        // This event happens when radio / connectivity is lost
//                    }
//
//                    else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
//
//                        // Connect event. You can do stuff like publish, and know you'll get it.
//                        // Or just use the connected event to confirm you are subscribed for
//                        // UI / internal notifications, etc
//
//                        if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
//                            pubnub.publish().channel("awesomeChannel").message("hello!!").async(new PNCallback<PNPublishResult>() {
//                                @Override
//                                public void onResponse(PNPublishResult result, PNStatus status) {
//                                    // Check whether request successfully completed or not.
//                                    if (!status.isError()) {
//
//                                        // Message successfully published to specified channel.
//                                    }
//                                    // Request processing failed.
//                                    else {
//
//                                        // Handle message publish error. Check 'category' property to find out possible issue
//                                        // because of which request did fail.
//                                        //
//                                        // Request can be resent using: [status retry];
//                                    }
//                                }
//                            });
//                        }
//                    }
//                    else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
//
//                        // Happens as part of our regular operation. This event happens when
//                        // radio / connectivity is lost, then regained.
//                    }
//                    else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
//
//                        // Handle messsage decryption error. Probably client configured to
//                        // encrypt messages and on live data feed it received plain text.
//                    }
                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    // Handle new message stored in message.message
                    if (message.getChannel() != null) {
                        // Message has been received on channel group stored in
                        // message.getChannel()
                    }
                    else {
                        // Message has been received on channel stored in
                        // message.getSubscription()
                    }

            /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscription()
                    - message.getTimetoken()
            */
                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                }
            });
        }
        return mPubNub;
    }

}
