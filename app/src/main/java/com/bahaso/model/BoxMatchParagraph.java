package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bahaso on 25/04/2017.
 */

public class BoxMatchParagraph implements Parcelable {
    String answer;
    String userAnswer;

    public String getAnswer() {
        return answer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public BoxMatchParagraph(String answer){
        this.answer = answer;
        userAnswer = "qws";
    }


    protected BoxMatchParagraph(Parcel in) {
        answer = in.readString();
        userAnswer = in.readString();
    }

    public static final Creator<BoxMatchParagraph> CREATOR = new Creator<BoxMatchParagraph>() {
        @Override
        public BoxMatchParagraph createFromParcel(Parcel in) {
            return new BoxMatchParagraph(in);
        }

        @Override
        public BoxMatchParagraph[] newArray(int size) {
            return new BoxMatchParagraph[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(answer);
        dest.writeString(userAnswer);
    }
}
