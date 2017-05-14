package com.bahaso.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context){
        this.context = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
//            boolean isConnected = info != null && info.isConnectedOrConnecting();
            boolean isConnected = info != null && info.isConnected();
            if(isConnected)
                return true;
        }
        return false;
    }

    public boolean isInternetAvailable(){
        try {
            URL url = new URL("http://google.com");   // Change to "http://google.com" for www  test.
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setConnectTimeout(10 * 1000);          // 10 s.
            urlc.connect();
            if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                Log.i("Connection tes1", "Success !");
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e1) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }


}
