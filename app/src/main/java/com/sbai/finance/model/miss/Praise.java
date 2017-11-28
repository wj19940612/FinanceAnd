package com.sbai.finance.model.miss;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.net.Client;

/**
 * Client /explain/question/prise.do  小姐姐点赞
 * {@link Client# /user/comment/priseReply.do}
 * 姐说问题详情页点赞
 */

public class Praise implements Parcelable {

    public static final int IS_PRAISE = 1;
    public static final int NOT_PRAISE = 0;

    /**
     * priseCount : 1
     * isPrise : 1
     */

    private int priseCount;
    private int isPrise;

    public int getPriseCount() {
        return priseCount;
    }

    public void setPriseCount(int priseCount) {
        this.priseCount = priseCount;
    }

    public int getIsPrise() {
        return isPrise;
    }

    public void setIsPrise(int isPrise) {
        this.isPrise = isPrise;
    }

    @Override
    public String toString() {
        return "Praise{" +
                "priseCount=" + priseCount +
                ", isPrise=" + isPrise +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.priseCount);
        dest.writeInt(this.isPrise);
    }

    public Praise() {
    }

    protected Praise(Parcel in) {
        this.priseCount = in.readInt();
        this.isPrise = in.readInt();
    }

    public static final Creator<Praise> CREATOR = new Creator<Praise>() {
        @Override
        public Praise createFromParcel(Parcel source) {
            return new Praise(source);
        }

        @Override
        public Praise[] newArray(int size) {
            return new Praise[size];
        }
    };
}
