package com.bahaso.util;

import com.bahaso.model.BoxMatchParagraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 25/04/2017.
 */

public class BoxMatchParagraphHelper {
    private List<BoxMatchParagraph> boxMatchParagraphList;
    private List<String> choices;


    public BoxMatchParagraphHelper(){
        boxMatchParagraphList = new ArrayList<>();
        choices = new ArrayList<>();
    }

    public  void add(BoxMatchParagraph boxMatchParagraph) {
        boxMatchParagraphList.add(boxMatchParagraph);
    }

    public  void addChoices(String choice) {
        choices.add(choice);
    }

    public   List<BoxMatchParagraph> getListData()
    {
        return boxMatchParagraphList;

    }

    public   List<String> getListChoices()
    {
        return choices;

    }
}
