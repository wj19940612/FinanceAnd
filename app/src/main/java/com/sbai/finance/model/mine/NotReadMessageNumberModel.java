package com.sbai.finance.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${wangJie} on 2017/4/26.
 */

public class NotReadMessageNumberModel implements Parcelable{

    /**
     * classify : 77008
     * count : 85148
     */

    private int classify;
    private int count;


    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "NotReadMessageNumberModel{" +
                "classify=" + classify +
                ", count=" + count +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.classify);
        dest.writeInt(this.count);
    }

    protected NotReadMessageNumberModel(Parcel in) {
        this.classify = in.readInt();
        this.count = in.readInt();
    }

    public static final Creator<NotReadMessageNumberModel> CREATOR = new Creator<NotReadMessageNumberModel>() {
        @Override
        public NotReadMessageNumberModel createFromParcel(Parcel source) {
            return new NotReadMessageNumberModel(source);
        }

        @Override
        public NotReadMessageNumberModel[] newArray(int size) {
            return new NotReadMessageNumberModel[size];
        }
    };
}
