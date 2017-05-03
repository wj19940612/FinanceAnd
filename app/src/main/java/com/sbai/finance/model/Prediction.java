package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified by JohnZ on 2017/5/3.
 */
public class Prediction implements Parcelable{

    public static final int DIRECTION_SHORT = 0;
    public static final int DIRECTION_LONG = 1;

    /**
     * calcuId : 1
     * direction : 0
     * isCalculate : true
     */
    private int calcuId; // 预测 id
    private int direction; // 方向0：跌 1 涨
    private boolean isCalculate; // 是否预测 true已预测，false没预测

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

    /**
     * 是否已经预测
     * @return true 已经预测
     */
    public boolean isCalculate() {
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
