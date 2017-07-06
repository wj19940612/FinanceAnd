package com.sbai.finance.model.future;

public class FutureTradeStatus {

    public static final int NOT_ALLOW_TRADE = 0;
    public static final int ALLOW_TRADE = 1;
    public static final String QUICE_TRADE = "quick";

    private int furtureDealStatus;
    private String furturesDealType;

    public int getFurtureDealStatus() {
        return furtureDealStatus;
    }
}
