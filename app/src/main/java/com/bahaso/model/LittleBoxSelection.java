package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bahaso on 23/03/2017.
 */

public class LittleBoxSelection implements Parcelable {


    String question="";
    ArrayList<ArrayList<String>> arrayListChoicesList = new ArrayList<>();
    ArrayList<String> arrayListAnswer=new ArrayList<>();
    ArrayList<String> arrayListUserAnswer = new ArrayList<>();

    protected LittleBoxSelection(Parcel in) {
        question = in.readString();
        arrayListAnswer = in.createStringArrayList();

    }

    public static final Creator<LittleBoxSelection> CREATOR = new Creator<LittleBoxSelection>() {
        @Override
        public LittleBoxSelection createFromParcel(Parcel in) {
            return new LittleBoxSelection(in);
        }

        @Override
        public LittleBoxSelection[] newArray(int size) {
            return new LittleBoxSelection[size];
        }
    };

    public String getQuestion() {
        return question;
    }

    public ArrayList<ArrayList<String>> getArrayListChoicesList() {
        return arrayListChoicesList;
    }

    public ArrayList<String> getAnswer() {
        return arrayListAnswer;
    }

    public ArrayList<String> getUserAnswer() {
        return arrayListUserAnswer;
    }

    public LittleBoxSelection(String question, ArrayList<ArrayList<String>> arrayListChoices,
                              ArrayList<String> answer){
        this.question = question;
        this.arrayListAnswer = answer;
        this.arrayListChoicesList = arrayListChoices;
        arrayListUserAnswer = new ArrayList<>();
        for(String ans: arrayListAnswer)
        {
            arrayListUserAnswer.add("");
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
    }
}
