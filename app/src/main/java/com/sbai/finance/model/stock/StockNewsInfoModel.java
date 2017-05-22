package com.sbai.finance.model.stock;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${wangJie} on 2017/5/19.
 * 单个新闻model
 */

public class StockNewsInfoModel implements Parcelable {

    /**
     * content : 测试内容h65x
     * createDate : 测试内容67p9
     * stockCode : 测试内容drf6
     * time : 测试内容o751
     * title : 测试内容jh16
     */

    private String content;
    private long createDate;
    private String stockCode;
    private long time;
    private String title;
    private String from;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeLong(this.createDate);
        dest.writeString(this.stockCode);
        dest.writeLong(this.time);
        dest.writeString(this.title);
        dest.writeString(this.from);
    }

    public StockNewsInfoModel() {
    }

    protected StockNewsInfoModel(Parcel in) {
        this.content = in.readString();
        this.createDate = in.readLong();
        this.stockCode = in.readString();
        this.time = in.readLong();
        this.title = in.readString();
        this.from = in.readString();
    }

    public static final Parcelable.Creator<StockNewsInfoModel> CREATOR = new Parcelable.Creator<StockNewsInfoModel>() {
        @Override
        public StockNewsInfoModel createFromParcel(Parcel source) {
            return new StockNewsInfoModel(source);
        }

        @Override
        public StockNewsInfoModel[] newArray(int size) {
            return new StockNewsInfoModel[size];
        }
    };
}
