package com.bahaso.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonHelper {

    private JsonHelper() {
        //no instance
    }

    static boolean hasCheckSupportMp4 = false;
    static boolean chooseMP4 = false;

    public static boolean chooseMP4(){
        if (! hasCheckSupportMp4) { // not checked yet
            String manufacturer = Build.MANUFACTURER.toLowerCase();
            String model = Build.MODEL.toLowerCase();

            if (manufacturer.contains("samsung")) {
                if (model.contains("gt") || Build.VERSION.SDK_INT <= 17) {
                    chooseMP4 = true;
                }
            }
            hasCheckSupportMp4 = true;
        }
        return chooseMP4;
    }

    public static String getJsonAttributeSupportedVideoSrc(){
        // if supported
        if (chooseMP4()) {
            return "video_src";
        }
        else {
            return "video_src_webm";
        }
    }

    /**
     * Creates a String array out of a json array.
     *
     * @param json The String containing the json array.
     * @return An array with the extracted strings or an
     * empty String array if an exception occurred.
     */
    public static String[] jsonArrayToStringArray(String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            String[] stringArray = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                stringArray[i] = jsonArray.getString(i);
            }
            return stringArray;
        } catch (JSONException e) {
            return new String[0];
        }
    }

    public static String[] jsonArrayToStringArray(JSONArray jsonArray) {
        if (null == jsonArray) return null;
        try {
            String[] stringArray = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                stringArray[i] = jsonArray.getString(i);
            }
            return stringArray;
        } catch (JSONException e) {
            return new String[0];
        }
    }

    public static String[][] jsonArrayToDoubleStringArray(String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            String[][] stringArray = new String[jsonArray.length()][];
            JSONArray jsonArrayLevel2;
            for (int i = 0,sizei = jsonArray.length(); i < sizei; i++) {
                if (null == jsonArray.get(i) || ("null").equals(jsonArray.getString(i))){
                    stringArray[i] = null;
                    continue;
                }
                jsonArrayLevel2 = jsonArray.getJSONArray(i);
                stringArray[i] = new String[jsonArrayLevel2.length()];
                for (int j = 0, sizej = jsonArrayLevel2.length(); j < sizej; j++) {
                    stringArray[i][j] = jsonArrayLevel2.getString(j);
                }
            }
            return stringArray;
        } catch (JSONException e) {
            return new String[0][0];
        }
    }
    public static String[][][] jsonArrayToTripleStringArray(String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            String[][][] stringArray = new String[jsonArray.length()][][];
            JSONArray jsonArrayLevel2;
            for (int i = 0,sizei = jsonArray.length(); i < sizei; i++) {
                if (null == jsonArray.get(i) || ("null").equals(jsonArray.getString(i))){
                    stringArray[i] = null;
                    continue;
                }
                jsonArrayLevel2 = jsonArray.getJSONArray(i);
                stringArray[i] = new String[jsonArrayLevel2.length()][];
                JSONArray jsonArrayLevel3;
                for (int j = 0, sizej = jsonArrayLevel2.length(); j < sizej; j++) {
                    if (null == jsonArrayLevel2.get(j) || ("null").equals(jsonArrayLevel2.getString(j))){
                        stringArray[i][j] = null;
                        continue;
                    }
                    jsonArrayLevel3 = jsonArrayLevel2.getJSONArray(j);
                    stringArray[i][j] = new String[jsonArrayLevel3.length()];
                    for (int k = 0, sizek = jsonArrayLevel3.length(); k < sizek; k++) {
                        stringArray[i][j][k] = jsonArrayLevel3.getString(k);
                    }
                }
            }
            return stringArray;
        } catch (JSONException e) {
            return new String[0][0][0];
        }
    }

    /**
     * Creates an int array out of a json array.
     *
     * @param json The String containing the json array.
     * @return An array with the extracted integers or an
     * empty int array if an exception occurred.
     */
    public static int[] jsonArrayToIntArray(String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            int[] intArray = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                intArray[i] = jsonArray.getInt(i);
            }
            return intArray;
        } catch (JSONException e) {
            return new int[0];
        }
    }

    public static int[][] jsonArrayToDoubleIntArray(String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            int[][] intArray = new int[jsonArray.length()][];
            JSONArray jsonArrayLevel2;
            for (int i = 0,sizei = jsonArray.length(); i < sizei; i++) {
                if (null == jsonArray.get(i) || ("null").equals(jsonArray.getString(i))){
                    intArray[i] = null;
                    continue;
                }
                jsonArrayLevel2 = jsonArray.getJSONArray(i);
                intArray[i] = new int[jsonArrayLevel2.length()];
                for (int j = 0, sizej = jsonArrayLevel2.length(); j < sizej; j++) {
                    intArray[i][j] = jsonArrayLevel2.getInt(j);
                }
            }
            return intArray;
        } catch (JSONException e) {
            return new int[0][0];
        }
    }

    // context is used to bind resource, to utilize garbage collector
    public static String readFromResource (Context context, int resource) {
        try {
            StringBuilder jsonString = new StringBuilder();
            InputStream rawInputStream = context.getResources().openRawResource(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawInputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return jsonString.toString();
        }
        catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }
    }
}

