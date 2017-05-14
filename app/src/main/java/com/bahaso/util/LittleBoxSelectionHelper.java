package com.bahaso.util;

import com.bahaso.model.LittleBoxSelection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 23/03/2017.
 */

public class LittleBoxSelectionHelper {
    private List<LittleBoxSelection> littleBoxSelectionList;

    public LittleBoxSelectionHelper(){
        littleBoxSelectionList = new ArrayList<>();
    }

    public  void add(LittleBoxSelection choosePicListen) {
        littleBoxSelectionList.add(choosePicListen);
    }

    public   List<LittleBoxSelection> getListData()
    {
        return littleBoxSelectionList;

    }
}
