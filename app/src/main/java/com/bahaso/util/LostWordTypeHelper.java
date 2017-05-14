package com.bahaso.util;

import com.bahaso.model.LostWordType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bahaso on 30/03/2017.
 */

public class LostWordTypeHelper {
    private List<LostWordType> lostWordTypeList;

    public LostWordTypeHelper(){
        lostWordTypeList = new ArrayList<>();
    }

    public  void add(LostWordType choosePicListen) {
        lostWordTypeList.add(choosePicListen);
    }

    public   List<LostWordType> getListData()
    {
        return lostWordTypeList;

    }
}
