package com.bahaso.model;

/**
 * Created by shiperus on 3/6/2017.
 */

public class LessonCountry {

    public String getLessonCountry() {
        return lessonCountry;
    }

    public String getLessonCountryImg() {
        return lessonCountryImg;
    }

    public String getPlacementTestId() {
        return placementTestId;
    }

    String lessonCountry;
    String lessonCountryImg;
    String placementTestId;

    public LessonCountry(String lessonCountry, String lessonCountryImg, String placementTestId) {
        this.lessonCountry = lessonCountry;
        this.lessonCountryImg = lessonCountryImg;
        this.placementTestId = placementTestId;
    }

}
