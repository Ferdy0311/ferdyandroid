package com.bahaso.util;

import com.bahaso.model.BoxMatchDisappearPicture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 07/04/2017.
 */

public class BoxMatchDisappearPictureHelper {
    private List<BoxMatchDisappearPicture> boxMatchDisappearPictureList;
    private List<String> imageChoicesString;


    public BoxMatchDisappearPictureHelper(){
        boxMatchDisappearPictureList = new ArrayList<>();
        imageChoicesString = new ArrayList<>();
    }

    public  void add(BoxMatchDisappearPicture boxMatchDisappearPicture) {
        boxMatchDisappearPictureList.add(boxMatchDisappearPicture);
    }

    public  void addImageString(String choice) {
        imageChoicesString.add(choice);
    }

    public   List<BoxMatchDisappearPicture> getListData()
    {
        return boxMatchDisappearPictureList;

    }

    public   List<String> getListImageChoices()
    {
        return imageChoicesString;

    }
}
