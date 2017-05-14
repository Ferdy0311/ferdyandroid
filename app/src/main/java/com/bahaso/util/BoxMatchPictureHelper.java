package com.bahaso.util;

import com.bahaso.model.BoxMatchPicture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 08/05/2017.
 */

public class BoxMatchPictureHelper {

    private List<BoxMatchPicture> boxMatchPictureList;
    private List<String> choices;


    public BoxMatchPictureHelper(){
        boxMatchPictureList = new ArrayList<>();
        choices = new ArrayList<>();
    }

    public  void add(BoxMatchPicture boxMatchPicture) {
        boxMatchPictureList.add(boxMatchPicture);
    }

    public  void addChoices(String choice) {
        choices.add(choice);
    }

    public   List<BoxMatchPicture> getListData()
    {
        return boxMatchPictureList;

    }

    public   List<String> getListChoices()
    {
        return choices;

    }
}
