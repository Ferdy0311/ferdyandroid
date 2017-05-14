package com.bahaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bahaso on 07/04/2017.
 */

public class BoxMatchDisappearPicture implements Parcelable{

    String question="";
    ArrayList<String> arrayListAnswer=new ArrayList<>();
    ArrayList<String> arrayListUserAnswer = new ArrayList<>();

    protected BoxMatchDisappearPicture(Parcel in) {
        question = in.readString();
        arrayListAnswer = in.createStringArrayList();
        arrayListUserAnswer = in.createStringArrayList();
    }

    public static final Creator<BoxMatchDisappearPicture> CREATOR = new Creator<BoxMatchDisappearPicture>() {
        @Override
        public BoxMatchDisappearPicture createFromParcel(Parcel in) {
            return new BoxMatchDisappearPicture(in);
        }

        @Override
        public BoxMatchDisappearPicture[] newArray(int size) {
            return new BoxMatchDisappearPicture[size];
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

    public BoxMatchDisappearPicture(String question, ArrayList<String> arrayListAnswer){
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
