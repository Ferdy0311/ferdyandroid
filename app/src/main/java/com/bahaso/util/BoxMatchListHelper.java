package com.bahaso.util;

import com.bahaso.model.BoxMatchList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 03/05/2017.
 */

public class BoxMatchListHelper {

    private List<BoxMatchList> listBoxMatchList;
    private List<String> choices;


    public BoxMatchListHelper(){
        listBoxMatchList = new ArrayList<>();
        choices = new ArrayList<>();
    }

    public  void add(BoxMatchList boxMatchList) {
        listBoxMatchList.add(boxMatchList);
    }

    public  void addChoices(String choice) {
        choices.add(choice);
    }

    public   List<BoxMatchList> getListData()
    {
        return listBoxMatchList;

    }

    public   List<String> getListChoices()
    {
        return choices;

    }

}
