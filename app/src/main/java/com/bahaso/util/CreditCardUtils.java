package com.bahaso.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by hendrysetiadi on 28/07/2016.
 */
public class CreditCardUtils {
    public static final int CARD_SIDE_FRONT = 1,CARD_SIDE_BACK=0;

    public static final String SPACE_SEPERATOR = " ";
    public static final String DOUBLE_SPACE_SEPERATOR = "  ";

    public static final String SLASH_SEPERATOR = "/";

    public static final int VISA = 1;
    public static final int MASTER_CARD = 2;

    public static int verifyCreditCardType(String creditCardNumber){
        if (TextUtils.isEmpty(creditCardNumber)) {
            return 0;
        }
        else if (creditCardNumber.startsWith("4")){
            return VISA;
        }
        if (creditCardNumber.length()>=4){
            int first4Digit = Integer.parseInt(creditCardNumber.substring(0,4) );
            if (first4Digit >= 2221 && first4Digit <= 2720) {
                return MASTER_CARD;
            }
        }
        if (creditCardNumber.length()>=2){
            int first2Digit = Integer.parseInt(creditCardNumber.substring(0,2) );
            if (first2Digit >= 51 && first2Digit <= 55) {
                return MASTER_CARD;
            }
        }
        return 0;
    }

    public static String handleCardNumber(String inputCardNumber) {

        return handleCardNumber(inputCardNumber,SPACE_SEPERATOR);
    }


    public static String handleCardNumber(String rawCardNumber, String seperator) {

        String text;

        if (rawCardNumber.length() >= 4) {

            text = rawCardNumber.substring(0, 4);

            if (rawCardNumber.length() >= 8) {
                text += seperator + rawCardNumber.substring(4, 8);
            } else if (rawCardNumber.length() > 4) {
                text += seperator + rawCardNumber.substring(4);
            }

            if (rawCardNumber.length() >= 12) {
                text += seperator + rawCardNumber.substring(8, 12);
            } else if (rawCardNumber.length() > 8) {
                text += seperator + rawCardNumber.substring(8);
            }

            if (rawCardNumber.length() >= 16) {
                text += seperator + rawCardNumber.substring(12);
            } else if (rawCardNumber.length() > 12) {
                text += seperator + rawCardNumber.substring(12);
            }

            return text;

        } else {
            text = rawCardNumber.trim();
        }

        return text;
    }


    public static String handleExpiration(String month, String year) {
        if (TextUtils.isEmpty(month)) {
            return "";
        }
        int monthInt = Integer.parseInt(month);
        String monthEdit;
        if (monthInt > 1 && monthInt < 10) {
            monthEdit = "0"+monthInt;
        }
        else if (monthInt > 12){
            monthEdit = "12";
        }
        else {
            monthEdit = month;
        }

        if (monthEdit.length() == 2) {
            monthEdit+= "/";
        }

        return monthEdit + year;
    }


    private static String handleExpiration(@NonNull String expiryString) {

        String text;
        if(expiryString.length() >= 2) {
            String mm = expiryString.substring(0, 2);
            String yyyy;
            text = mm;

            try {
                if (Integer.parseInt(mm) > 12) {
                    mm = "12"; // Cannot be more than 12.
                }
            }
            catch (Exception e) {
                mm = "01";
            }

            if(expiryString.length() >=6) {
                yyyy = expiryString.substring(2,6);

                try{
                    Integer.parseInt(yyyy);
                }catch (Exception e) {

                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    yyyy = String.valueOf(year).substring(4);
                }

                text = mm + SLASH_SEPERATOR + yyyy;

            }
            else if(expiryString.length() > 2){
                yyyy = expiryString.substring(2);
                text = mm + SLASH_SEPERATOR + yyyy;
            }
//            else if(expiryString.length() == 2){
//                text = mm + SLASH_SEPERATOR;
//            }
        }
        else {
            text = expiryString;
        }

        return text;
    }
}