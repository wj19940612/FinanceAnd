package com.sbai.finance.model;

import java.util.List;

public class ListWrapper<T> {

    /**
     * data : [{"displayMarketTimes":"21:00;00:00;09:00;13:30;15:00","smallVarietyTypeCode":"china","decimalScale":0.2,"varietyType":"ag","bigVarietyTypeCode":"future","sort":2,"baseline":2,"exchangeId":1,"openMarketTime":"07:00;06:00","flashChartPriceInterval":9,"varietyId":8,"exchangeStatus":1,"contractsCode":"ag1709","marketPoint":0,"varietyName":"沪银"}]
     * pageSize : 15
     * resultCount : 1
     * start : 0
     * total : 1
     */

    private int pageSize;
    private int resultCount;
    private int start;
    private int total;

    private List<T> data;

    public int getPageSize() {
        return pageSize;
    }

    public int getResultCount() {
        return resultCount;
    }

    public int getStart() {
        return start;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getData() {
        return data;
    }
}
