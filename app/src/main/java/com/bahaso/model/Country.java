package com.bahaso.model;

import android.content.Context;
import android.support.annotation.NonNull;


public class Country implements Comparable<Country>{
    String code;
    String name;
    int callingCode;

    public Country(String code, String name, int callingCode) {
        this.code = code;
        this.name = name;
        this.callingCode = callingCode;
    }

    // USED WHEN COLLECTION.SORT
    @Override
    public int compareTo(@NonNull Country another) {
        return name.compareTo(another.getName());
    }

    public int getCallingCode() {
        return callingCode;
    }

    public String getCallingCodeString() {
        return "+"+callingCode;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getFlagResource(Context context){
        String mDrawableName = "flag_" + code.toLowerCase();
        return context.getResources().getIdentifier(mDrawableName, "drawable", context.getPackageName());
    }

}
