package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bahaso on 08/05/2017.
 */

public class BoxMatchPicture implements Parcelable{

    String imageSrc;
    String answer;
    String userAnswer;

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public String getAnswer() {
        return answer;
    }

    public BoxMatchPicture(String imageSrc, String answer){
        this.imageSrc = imageSrc;
        this.answer = answer;
        userAnswer = "";

    }

    protected BoxMatchPicture(Parcel in) {
        imageSrc = in.readString();
        answer = in.readString();
    }

    public static final Creator<BoxMatchPicture> CREATOR = new Creator<BoxMatchPicture>() {
        @Override
        public BoxMatchPicture createFromParcel(Parcel in) {
            return new BoxMatchPicture(in);
        }

        @Override
        public BoxMatchPicture[] newArray(int size) {
            return new BoxMatchPicture[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageSrc);
        dest.writeString(answer);
    }
}
