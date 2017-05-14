package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bahaso on 30/03/2017.
 */

public class LostWordType implements Parcelable  {
    protected LostWordType(Parcel in) {
        question = in.readString();
        arrayListAnswer = in.createStringArrayList();
        arrayListUserAnswer = in.createStringArrayList();
    }

    public static final Creator<LostWordType> CREATOR = new Creator<LostWordType>() {
        @Override
        public LostWordType createFromParcel(Parcel in) {
            return new LostWordType(in);
        }

        @Override
        public LostWordType[] newArray(int size) {
            return new LostWordType[size];
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

    String question="";
    ArrayList<String> arrayListAnswer;
    ArrayList<String> arrayListUserAnswer;

    public ArrayList<Boolean> getArrayListEditTextState() {
        return arrayListEditTextState;
    }

    ArrayList<Boolean> arrayListEditTextState;



    public LostWordType(String question, ArrayList<String> arrayListAnswer ){
        this.question = question;
        this.arrayListAnswer = arrayListAnswer;
        arrayListUserAnswer = new ArrayList<>();
        arrayListEditTextState = new ArrayList<>();

        for(String ans :arrayListAnswer )
        {

            arrayListUserAnswer.add("");
            arrayListEditTextState.add(false);
        }
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
