package com.sbai.finance.model.klinebattle;

import com.sbai.chart.domain.KlineViewData;

/**
 * Modified by john on 13/12/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class BattleKlineData extends KlineViewData {

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
}
