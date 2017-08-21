package com.sbai.finance.model.training;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 训练目标
 */
public class TrainingTarget implements Parcelable {
    // 1 按时间 2 按概率 3完成即可
    public static final int TYPE_TIME = 1;
    public static final int TYPE_RATE = 2;
    public static final int TYPE_FINISH = 3;

    /**
     * id : 5
     * level : 1
     * rate : 0.5
     * score : 5
     * time : 100
     * trainId : 5
     * type : 1
     */

    private int id;
    private int level;
    private double rate;
    private int score;
    private int time;
    private int trainId;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TrainingTarget{" +
                "id=" + id +
                ", level=" + level +
                ", rate=" + rate +
                ", score=" + score +
                ", time=" + time +
                ", trainId=" + trainId +
                ", type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.level);
        dest.writeDouble(this.rate);
        dest.writeInt(this.score);
        dest.writeInt(this.time);
        dest.writeInt(this.trainId);
        dest.writeInt(this.type);
    }

    public TrainingTarget() {
    }

    protected TrainingTarget(Parcel in) {
        this.id = in.readInt();
        this.level = in.readInt();
        this.rate = in.readDouble();
        this.score = in.readInt();
        this.time = in.readInt();
        this.trainId = in.readInt();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<TrainingTarget> CREATOR = new Parcelable.Creator<TrainingTarget>() {
        @Override
        public TrainingTarget createFromParcel(Parcel source) {
            return new TrainingTarget(source);
        }

        @Override
        public TrainingTarget[] newArray(int size) {
            return new TrainingTarget[size];
        }
    };
}
