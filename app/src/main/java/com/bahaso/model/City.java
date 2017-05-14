package com.bahaso.model;

import android.support.annotation.NonNull;

public class City implements Comparable<City>{

    String name;
    String cityId;
    String type;
    String countryId;

    public City(String cityName, String cityId, String type, String countryId){
        this.name = cityName;
        this.cityId = cityId;
        this.type = type;
        this.countryId = countryId;
    }

    @Override
    public int compareTo(@NonNull City another){
        return name.compareTo(another.getName());
    }

    public String getName() {
        return name;
    }

    public String getCityId() {
        return cityId;
    }

    public String getType() {
        return type;
    }

    public String getCountryId() {
        return countryId;
    }
}
