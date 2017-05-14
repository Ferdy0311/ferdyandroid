package com.bahaso.util;

import android.content.Context;
import android.text.TextUtils;

import com.bahaso.R;
import com.bahaso.model.City;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CityHelper {
    static String URL_CITY = "http://dc33c8b0a252f4219c9698652d4e4708.bahaso.com/cities/";

    private static List<City> mCityList = new ArrayList<>();


    public static List<City> getCityList(Context context) {
        if(mCityList.size() == 0){
            String jsonCities = JsonHelper.readFromResource(context, R.raw.cities);
            if(!TextUtils.isEmpty(jsonCities)){
                try{
                    JSONArray jsonCitiesArray = new JSONArray(jsonCities);
                    for(int i = 0, size = jsonCitiesArray.length(); i < size; i++){
                        JSONObject jsonCityObject = jsonCitiesArray.getJSONObject(i);
                        String cityName = jsonCityObject.getString("name");
                        String cityId = jsonCityObject.getString("raw_city_id");
                        String type = jsonCityObject.getString("type");
                        String countryId = jsonCityObject.getString("country_iso_3166_2");
                        City city = new City(cityName, cityId, type, countryId);
                        mCityList.add(city);
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    return null;
                }
            }
        }
        return mCityList;
    }
}
