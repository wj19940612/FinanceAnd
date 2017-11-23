package com.sbai.finance.model.stocktrade;

import java.util.List;

/**
 * 持仓
 */

public class PositionRecords {

    /**
     * usableDraw : null
     * usableMoney : 108775.72
     * list : [{"id":1,"version":1,"positionType":2,"virtualType":1,"userId":689,"userAccount":"jf100009","market":"4609","varietyCode":"002158","varietyName":"汉钟精机","avgBuyPrice":14.043,"totalQty":4600,"usableQty":4600,"activityCode":null,"frozenQty":0,"createTime":1508477842000,"updateTime":1510128262000,"todayAvgPrice":11.114285714285714}]
     */

    private double usableDraw;
    private double usableMoney;
    private List<Position> list;

    public double getUsableDraw() {
        return usableDraw;
    }

    public void setUsableDraw(double usableDraw) {
        this.usableDraw = usableDraw;
    }

    public double getUsableMoney() {
        return usableMoney;
    }

    public void setUsableMoney(double usableMoney) {
        this.usableMoney = usableMoney;
    }

    public List<Position> getList() {
        return list;
    }

    public void setList(List<Position> list) {
        this.list = list;
    }
}
