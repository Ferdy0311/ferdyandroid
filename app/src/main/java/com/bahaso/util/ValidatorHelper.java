package com.bahaso.util;

//import com.bahaso.activity.appCompatActivity.MyAppCompatNewActivity;


import android.text.TextUtils;

public class ValidatorHelper {
    public static boolean isValidEmailAddress(String email) {
//        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
//        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
//        java.util.regex.Matcher m = p.matcher(email);
//        return m.matches();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidCharFirstName(String firstName) {
        String pattern= "^[a-zA-Z ']*$";
        if(firstName.matches(pattern)){
            return true;
        }
        return false;
    }

    public static boolean isValidCharLastName(String lastName) {
        String pattern= "^[a-zA-Z '.-]*$";
        if(lastName.matches(pattern)){
            return true;
        }
        return false;
    }

    public static String omitPunctuationAndDoubleSpace(String stringToReplace){
        if (TextUtils.isEmpty(stringToReplace)){
            return "";
        }
        else {
            return stringToReplace.replaceAll("\\r|\\n", " ")
                    .replaceAll("\\s+", " ")
                    .replaceAll("[^0-9a-zA-Z ]", "")
                    .trim();
        }
    }

    public static String omitNonCharacters(String stringToReplace){
        return stringToReplace.replaceAll("[^0-9a-zA-Z ]", "")
                .trim();
    }

//    public static String spanToFont(String stringToReplace){
//        return stringToReplace.replaceAll("<span style=\"color:|<span style='color:", "<font color=\"")
//                                .replaceAll(";\">|;'>|'>|\">", "\">")
//                                .replaceAll("</span>", "</font>");
//    }

//    public static boolean checkValidActivityListener(Object listener){
//        if (null == listener) {
//            return false;
//        }
//        if (listener instanceof MyAppCompatNewActivity) {
//            MyAppCompatNewActivity act = (MyAppCompatNewActivity) listener;
//            if (act.isPaused()) {
//                return false;
//            }
//        }
//        if (listener instanceof Fragment) {
//            Fragment f = (Fragment) listener;
//            Activity act = f.getActivity();
//            if (null == act) {
//                return false;
//            }
//            if (act instanceof MyAppCompatNewActivity) {
//                MyAppCompatNewActivity act2 = (MyAppCompatNewActivity) act;
//                if (act2.isPaused()) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
}
