package com.sbai.finance.model;

/**
 * Created by linrongfang on 2017/4/28.
 */

public class Prediction {

    //预测id
    public static final String PREDICT_CALCUID = "predict_calcuid";
    //预测方向
    public static final String PREDICT_DIRECTION = "predict_direction";

    /**
     * calcuId : 1
     * direction : 0
     * isCalculate : true
     */

    private int calcuId;
    private int direction;
    private boolean isCalculate;

    public int getCalcuId() {
        return calcuId;
    }

    public void setCalcuId(int calcuId) {
        this.calcuId = calcuId;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isIsCalculate() {
        return isCalculate;
    }

    public void setIsCalculate(boolean isCalculate) {
        this.isCalculate = isCalculate;
    }
}
