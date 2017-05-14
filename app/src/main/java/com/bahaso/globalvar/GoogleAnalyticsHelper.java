package com.bahaso.globalvar;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ASUS on 2/24/2017.
 */

public class GoogleAnalyticsHelper {

    private Tracker mGaTracker = null;
    private static String TAG = "GoogleAnalytics tes1";
//    private static final String PROPERTY_ID = "UA-92480491-";
    private static final String PROPERTY_ID = "UA-66234596-1";

    //    public GoogleAnalyticsHelper() {
    public void initialize(Context ctx) {
        try {

            if (mGaTracker == null && ctx != null) {
                mGaTracker = GoogleAnalytics.getInstance(ctx).newTracker(PROPERTY_ID);
            }
        } catch (Exception e) {
            Log.d(GoogleAnalyticsHelper.TAG, "init, e=" + e);
        }
    }

    public void SendScreenNameGoogleAnalytics(String SCREEN_NAME, Context iCtx) {
        initialize(iCtx);
        mGaTracker.setScreenName(SCREEN_NAME);
        //noinspection deprecation
        mGaTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public void SendEventGoogleAnalytics(Context iCtx, String iCategoryId, String iActionId, String iLabelId) {
        initialize(iCtx);
        // Build and send an Event.
        mGaTracker.send(new HitBuilders.EventBuilder()
                .setCategory(iCategoryId)
                .setAction(iActionId)
                .setLabel(iLabelId)
                .build());
    }
}


