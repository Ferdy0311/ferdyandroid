package com.bahaso.model;

/**
 * Created by hendrysetiadi on 27/07/2016.
 */
public class PayBankGroup {
    String name;
    int imageRes;
    String [] stepInfos;
//    String stepInfos;
    public PayBankGroup(String name, int imageRes, String[] stepInfos) {
        this.name = name;
        this.imageRes = imageRes;
        this.stepInfos = stepInfos;
    }

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getStepInfo(int stepInfoNo) {
        try {
            return stepInfos[stepInfoNo];
        }
        catch (Exception e) {
            return null;
        }
    }

    public int getStepInfoCount() {
        if (null == stepInfos) return 0;
        return stepInfos.length;
    }


//    public String getStepInfos() {
//        return stepInfos;
//    }
}
