package com.sbai.chart.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class KlineViewData implements Parcelable {

    /**
     * closePrice : 1179.2
     * day : 2016-12-03
     * maxPrice : 1180.3
     * minPrice : 1168.4
     * openPrice : 1174.3
     * time : 2016-12-03 06:01:33
     * timeStamp : 1480716093535
     */

    private float closePrice;
    private float maxPrice;
    private float minPrice;
    private float openPrice;
    private long nowVolume;
    private String day;
    private String time;
    private long timeStamp;

    // local cache data
    private SparseArray<Float> movingAverages;

    public float getClosePrice() {
        return closePrice;
    }

    public String getDay() {
        return day;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public String getTime() {
        return time;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getNowVolume() {
        return nowVolume;
    }

    public void addMovingAverage(int movingAverageKey, float movingAverageValue) {
        if (movingAverages == null) {
            movingAverages = new SparseArray<>();
        }
        movingAverages.put(movingAverageKey, Float.valueOf(movingAverageValue));
    }

    public Float getMovingAverage(int movingAverageKey) {
        if (movingAverages != null) {
            return movingAverages.get(movingAverageKey);
        }
        return null;
    }

    public float getMovingAverageValue(int movingAverageKey) {
        if (movingAverages != null) {
            Float aFloat = movingAverages.get(movingAverageKey);
            if (aFloat != null) {
                return aFloat.floatValue();
            }
            return 0f;
        }
        return 0f;
    }

    @Override
    public String toString() {
        return "KlineViewData{" +
                "closePrice=" + closePrice +
                ", maxPrice=" + maxPrice +
                ", minPrice=" + minPrice +
                ", openPrice=" + openPrice +
                ", time='" + time + '\'' +
                ", day='" + day + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.closePrice);
        dest.writeFloat(this.maxPrice);
        dest.writeFloat(this.minPrice);
        dest.writeFloat(this.openPrice);
        dest.writeLong(this.nowVolume);
        dest.writeString(this.day);
        dest.writeString(this.time);
        dest.writeLong(this.timeStamp);
        dest.writeSparseArray((SparseArray) this.movingAverages);
    }

    public KlineViewData() {
    }

    protected KlineViewData(Parcel in) {
        this.closePrice = in.readFloat();
        this.maxPrice = in.readFloat();
        this.minPrice = in.readFloat();
        this.openPrice = in.readFloat();
        this.nowVolume = in.readLong();
        this.day = in.readString();
        this.time = in.readString();
        this.timeStamp = in.readLong();
        this.movingAverages = in.readSparseArray(Float.class.getClassLoader());
    }

    public static final Parcelable.Creator<KlineViewData> CREATOR = new Parcelable.Creator<KlineViewData>() {
        @Override
        public KlineViewData createFromParcel(Parcel source) {
            return new KlineViewData(source);
        }

        @Override
        public KlineViewData[] newArray(int size) {
            return new KlineViewData[size];
        }
    };
}
