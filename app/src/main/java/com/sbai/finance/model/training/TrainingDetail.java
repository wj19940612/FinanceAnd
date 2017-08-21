package com.sbai.finance.model.training;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 训练详情：由训练对象，训练目标对象
 */
public class TrainingDetail implements Parcelable {

    private List<TrainingTarget> targets;
    private Training train;

    public List<TrainingTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<TrainingTarget> targets) {
        this.targets = targets;
    }

    public Training getTrain() {
        return train;
    }

    public void setTrain(Training train) {
        this.train = train;
    }

    @Override
    public String toString() {
        return "TrainingDetail{" +
                "targets=" + targets +
                ", train=" + train +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.targets);
        dest.writeParcelable(this.train, flags);
    }

    public TrainingDetail() {
    }

    protected TrainingDetail(Parcel in) {
        this.targets = in.createTypedArrayList(TrainingTarget.CREATOR);
        this.train = in.readParcelable(Training.class.getClassLoader());
    }

    public static final Parcelable.Creator<TrainingDetail> CREATOR = new Parcelable.Creator<TrainingDetail>() {
        @Override
        public TrainingDetail createFromParcel(Parcel source) {
            return new TrainingDetail(source);
        }

        @Override
        public TrainingDetail[] newArray(int size) {
            return new TrainingDetail[size];
        }
    };
}
