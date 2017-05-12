package com.sbai.finance.model.market;

/**
 * Created by Administrator on 2017-05-12.
 */

public class StockSearchData {

    /**
     * instrumentId : 399001
     * name : 深证成指
     * type : 1
     */

    private String instrumentId;
    private String name;
    private int type;

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
