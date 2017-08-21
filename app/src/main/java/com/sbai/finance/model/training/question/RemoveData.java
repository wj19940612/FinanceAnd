package com.sbai.finance.model.training.question;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.model.training.RemoveTraining;

/**
 * 消消乐数据
 */

public class RemoveData implements Parcelable {
    private RemoveTraining key;
    private RemoveTraining value;

    public RemoveTraining getKey() {
        return key;
    }

    public void setKey(RemoveTraining key) {
        this.key = key;
    }

    public RemoveTraining getValue() {
        return value;
    }

    public void setValue(RemoveTraining value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.key, flags);
        dest.writeParcelable(this.value, flags);
    }

    public RemoveData() {
    }

    protected RemoveData(Parcel in) {
        this.key = in.readParcelable(RemoveTraining.class.getClassLoader());
        this.value = in.readParcelable(RemoveTraining.class.getClassLoader());
    }

    public static final Parcelable.Creator<RemoveData> CREATOR = new Parcelable.Creator<RemoveData>() {
        @Override
        public RemoveData createFromParcel(Parcel source) {
            return new RemoveData(source);
        }

        @Override
        public RemoveData[] newArray(int size) {
            return new RemoveData[size];
        }
    };
}
