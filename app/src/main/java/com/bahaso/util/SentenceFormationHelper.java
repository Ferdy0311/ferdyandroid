package com.bahaso.util;

import com.bahaso.model.SentenceFormation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 25/04/2017.
 */

public class SentenceFormationHelper {
    private List<SentenceFormation> sentenceFormationList;
    private List<String> choices;


    public SentenceFormationHelper(){
        sentenceFormationList = new ArrayList<>();
        choices = new ArrayList<>();
    }

    public  void add(SentenceFormation sentenceFormation) {
        sentenceFormationList.add(sentenceFormation);
    }

    public  void addChoices(String choice) {
        choices.add(choice);
    }

    public   List<SentenceFormation> getListData()
    {
        return sentenceFormationList;

    }

    public   List<String> getListChoices()
    {
        return choices;

    }
}
