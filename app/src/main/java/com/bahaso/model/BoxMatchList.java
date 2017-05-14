package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bahaso on 03/05/2017.
 */

public class BoxMatchList implements Parcelable {
    String question="";
    ArrayList<String> arrayListAnswer;
    ArrayList<String> arrayListUserAnswer;

    public BoxMatchList(String question, ArrayList<String> arrayListAnswer ){
        this.question = question;
        this.arrayListAnswer = arrayListAnswer;
        arrayListUserAnswer = new ArrayList<>();

        for(String ans :arrayListAnswer )
        {
            arrayListUserAnswer.add("");
        }
    }

    protected BoxMatchList(Parcel in) {
        question = in.readString();
        arrayListAnswer = in.createStringArrayList();
        arrayListUserAnswer = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringList(arrayListAnswer);
        dest.writeStringList(arrayListUserAnswer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BoxMatchList> CREATOR = new Creator<BoxMatchList>() {
        @Override
        public BoxMatchList createFromParcel(Parcel in) {
            return new BoxMatchList(in);
        }

        @Override
        public BoxMatchList[] newArray(int size) {
            return new BoxMatchList[size];
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
}
