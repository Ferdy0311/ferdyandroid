package com.bahaso.fcm;

/**
 * Created by hendrysetiadi on 07/12/2016.
 */

public class BahasoNotificationFCM {
    public static String TYPE = "type";
    public static String WRITING_ID = "writing_id";
    public static String MESSAGE = "message";

    public static String TYPE_GOLD = "gold";
    public static String TYPE_REVIEW = "review";

    public static String getBahasoChannel(){
        return "bahaso";
    }
    public static String getUserChannel(String userId){
        return userId;
    }
}
