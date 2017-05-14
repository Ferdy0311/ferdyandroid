package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bahaso on 07/04/2017.
 */

public class BoxMatchDisappear implements Parcelable{

    String question="";
    ArrayList<String> arrayListAnswer=new ArrayList<>();
    ArrayList<String> arrayListUserAnswer = new ArrayList<>();

    protected BoxMatchDisappear(Parcel in) {
        question = in.readString();
        arrayListAnswer = in.createStringArrayList();
        arrayListUserAnswer = in.createStringArrayList();
    }

    public static final Creator<BoxMatchDisappear> CREATOR = new Creator<BoxMatchDisappear>() {
        @Override
        public BoxMatchDisappear createFromParcel(Parcel in) {
            return new BoxMatchDisappear(in);
        }

        @Override
        public BoxMatchDisappear[] newArray(int size) {
            return new BoxMatchDisappear[size];
        }
    };

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getArrayListAnswer() {
        return arrayListAnswer;
    }

    public ArrayList<String> getArrayListUserAnswer() {
        return arrayListUserAnswer;
    }

    public BoxMatchDisappear(String question, ArrayList<String> arrayListAnswer){
        this.question = question;
        this.arrayListAnswer = arrayListAnswer;
        arrayListUserAnswer = new ArrayList<>();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringList(arrayListAnswer);
        dest.writeStringList(arrayListUserAnswer);
    }
}
