package com.sbai.finance.model.training;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 提交训练 model
 */
public class TrainingSubmit implements Parcelable {

    private int trainId;
    private int time; // 完成训练时间
    private boolean isFinish; // 是否完成训练
    private double rate; // 以正确率为指标的训练里的 正确率

    public TrainingSubmit(int trainId) {
        this.trainId = trainId;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.trainId);
        dest.writeInt(this.time);
        dest.writeByte(this.isFinish ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.rate);
    }

    protected TrainingSubmit(Parcel in) {
        this.trainId = in.readInt();
        this.time = in.readInt();
        this.isFinish = in.readByte() != 0;
        this.rate = in.readDouble();
    }

    public static final Parcelable.Creator<TrainingSubmit> CREATOR = new Parcelable.Creator<TrainingSubmit>() {
        @Override
        public TrainingSubmit createFromParcel(Parcel source) {
            return new TrainingSubmit(source);
        }

        @Override
        public TrainingSubmit[] newArray(int size) {
            return new TrainingSubmit[size];
        }
    };
}
