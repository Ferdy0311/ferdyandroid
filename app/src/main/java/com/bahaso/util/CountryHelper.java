package com.bahaso.util;

import android.content.Context;
import android.text.TextUtils;

import com.bahaso.R;
import com.bahaso.model.Country;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CountryHelper {
    private static List<Country> mCountryList = new ArrayList<>();

//    public static Country getCountryByCode (String countryCode) {
//        if (getCountryList() != null) {
//            for (int i=0, sizei = mCountryList.size(); i<sizei; i++){
//                if (mCountryList.get(i).getCode().equals(countryCode)){
//                    return mCountryList.get(i);
//                }
//            }
//        }
//        return null;
//    }

    public static List<Country> getCountryList(Context context) {
        if (mCountryList.size() == 0) {
            String jsonCountries = JsonHelper.readFromResource(context, R.raw.countries);
            if (!TextUtils.isEmpty(jsonCountries)) {
                try {
                    JSONArray jsonCountriesArray = new JSONArray(jsonCountries);
                    for (int i = 0, sizei = jsonCountriesArray.length(); i<sizei; i++) {
                        JSONObject jsonCountryObject = jsonCountriesArray.getJSONObject(i);
                        String countryCode = jsonCountryObject.getString("id");
                        String countryName = jsonCountryObject.getString("name");
                        int callingCode = jsonCountryObject.getInt("calling_code");
                        Country country = new Country(countryCode, countryName, callingCode);
                        mCountryList.add(country);
                    }
                }
                catch (Exception e) {
                    Crashlytics.logException(e);
                    return null;
                }
            }
        }
        return mCountryList;
    }

    public static void removeCountryList(){
        mCountryList.clear();
    }

    //    public static int searchCountry (String countryCode){
//        int size = mCountryList.size();
//        if (size == 0){
//            return -1;
//        }
//
//        int lowerBound = 0;
//        int upperBound = size - 1;
//        Country countrySearch;
//        String countrySearchCode;
//
//        // search binary
//        while(lowerBound < (upperBound - 1)){
//            int middle = ( lowerBound + upperBound ) /2;
//            countrySearch = mCountryList.get(middle);
//            countrySearchCode = countrySearch.getCode();
//            if (countrySearchCode.equals(countryCode)) {
//                return middle;
//            }
//            else if (countrySearchCode.compareTo(countryCode) < 0){
//                if (lowerBound == middle) break;
//                lowerBound = middle;
//            }
//            else {
//                if (upperBound == middle) break;
//                upperBound = middle;
//            }
//        }
//
//        // end of search
//        // check lower and upper bound
//        // the dfference LB and UB is one maximum.
//
//        // check lower bound
//        countrySearch = mCountryList.get(lowerBound);
//        countrySearchCode = countrySearch.getCode();
//        if (countrySearchCode.equals(countryCode)) {
//            return lowerBound;
//        }
//        else if (countrySearchCode.compareTo(countryCode) > 0){
//            return -1; // less than lower bound
//        }
//
//        // if upper = lower, we have checked lower, so no need to check upper.
//        if (upperBound == lowerBound) {
//            return -1;
//        }
//
//        // check upper bound
//        countrySearch = mCountryList.get(upperBound);
//        countrySearchCode = countrySearch.getCode();
//        if (countrySearchCode.equals(countryCode)) {
//            return upperBound;
//        }
//        // not found
//        return -1;
//    }
}
