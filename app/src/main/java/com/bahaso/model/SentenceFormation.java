package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bahaso on 10/05/2017.
 */

public class SentenceFormation implements Parcelable{
    String answer;
    String userAnswer;

    public SentenceFormation(String answer){
        this.answer = answer;
        userAnswer = "";
    }

    protected SentenceFormation(Parcel in) {
        answer = in.readString();
        userAnswer = in.readString();
    }

    public static final Creator<SentenceFormation> CREATOR = new Creator<SentenceFormation>() {
        @Override
        public SentenceFormation createFromParcel(Parcel in) {
            return new SentenceFormation(in);
        }

        @Override
        public SentenceFormation[] newArray(int size) {
            return new SentenceFormation[size];
        }
    };

    public String getAnswer() {
        return answer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

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
