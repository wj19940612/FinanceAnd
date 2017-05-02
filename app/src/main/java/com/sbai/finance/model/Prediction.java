package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by linrongfang on 2017/4/28.
 */
public class Prediction implements Parcelable{

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.calcuId);
        dest.writeInt(this.direction);
        dest.writeByte(this.isCalculate ? (byte) 1 : (byte) 0);
    }

    public Prediction() {
    }

    protected Prediction(Parcel in) {
        this.calcuId = in.readInt();
        this.direction = in.readInt();
        this.isCalculate = in.readByte() != 0;
    }

    public static final Creator<Prediction> CREATOR = new Creator<Prediction>() {
        @Override
        public Prediction createFromParcel(Parcel source) {
            return new Prediction(source);
        }

        @Override
        public Prediction[] newArray(int size) {
            return new Prediction[size];
        }
    };
}
