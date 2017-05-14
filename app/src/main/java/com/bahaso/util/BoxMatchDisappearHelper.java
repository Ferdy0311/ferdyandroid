package com.bahaso.util;

import com.bahaso.model.BoxMatchDisappear;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 25/04/2017.
 */

public class BoxMatchDisappearHelper {
    private List<BoxMatchDisappear> boxMatchDisappearList;
    private List<String> choices;


    public BoxMatchDisappearHelper(){
        boxMatchDisappearList = new ArrayList<>();
        choices = new ArrayList<>();
    }

    public  void add(BoxMatchDisappear boxMatchDisappear) {
        boxMatchDisappearList.add(boxMatchDisappear);
    }

    public  void addChoices(String choice) {
        choices.add(choice);
    }

    public   List<BoxMatchDisappear> getListData()
    {
        return boxMatchDisappearList;

    }

    public   List<String> getListChoices()
    {
        return choices;

    }
}
