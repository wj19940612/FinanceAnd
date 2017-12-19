package com.sbai.finance.model.klinebattle;

import android.os.Parcel;

import com.sbai.chart.domain.KlineViewData;

/**
 * Modified by john on 13/12/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class BattleKlineData extends KlineViewData {

    public static final String MARK_NEW = "Y";
    public static final String MARK_BUY = "B";
    public static final String MARK_SELL = "S";
    public static final String MARK_PASS = "P";
    public static final String MARK_HOLD_PASS = "HP";

    private int id;
    private String mark;
    private double profit;
    private double positions;

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setPositions(double positions) {
        this.positions = positions;
    }

    public String getMark() {
        return mark;
    }

    public double getProfit() {
        return profit;
    }

    public double getPositions() {
        return positions;
    }

    public BattleKlineData(KlineViewData klineViewData) {
        super(klineViewData);
        mark = MARK_PASS;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeString(this.mark);
        dest.writeDouble(this.profit);
        dest.writeDouble(this.positions);
    }

    protected BattleKlineData(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.mark = in.readString();
        this.profit = in.readDouble();
        this.positions = in.readDouble();
    }

    public static final Creator<BattleKlineData> CREATOR = new Creator<BattleKlineData>() {
        @Override
        public BattleKlineData createFromParcel(Parcel source) {
            return new BattleKlineData(source);
        }

        @Override
        public BattleKlineData[] newArray(int size) {
            return new BattleKlineData[size];
        }
    };
}