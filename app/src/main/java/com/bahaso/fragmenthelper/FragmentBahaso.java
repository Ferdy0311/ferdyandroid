package com.bahaso.fragmenthelper;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by bahaso on 21/03/2017.
 */

public abstract class FragmentBahaso extends Fragment{


    protected boolean isFullAnswered = false;
    public String mainType = "case";

    public  void playVideo(){}
    public  void pauseVideo(){}
    public  void playAudio(){}
    public  void pauseAudio(){}

    public List getList(){return null;}

    public void setType(String type){};
    public String getType(){return null;}
    public void checkAnswer(){}
    public void checkFullAnswered(){}

    public boolean getIsFullAnswered(){ return isFullAnswered;}


    public boolean getIsChecked() {
        return false;
    }

    public void setChecked(boolean checked) {
    }


    public boolean getIsCaseCorrect(){return false;}
}
