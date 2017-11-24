package com.sbai.finance.model.stocktrade;

/**
 * 持仓和资金
 */

public class FundAndPosition {
    private double todayProfit;
    private double floatProfit;
    private double totalMarket;
    private double enableFund;
    private double fetchFund;

    public double getTodayProfit() {
        return todayProfit;
    }

    public void setTodayProfit(double todayProfit) {
        this.todayProfit = todayProfit;
    }

    public double getFloatProfit() {
        return floatProfit;
    }

    public void setFloatProfit(double floatProfit) {
        this.floatProfit = floatProfit;
    }

    public double getTotalMarket() {
        return totalMarket;
    }

    public void setTotalMarket(double totalMarket) {
        this.totalMarket = totalMarket;
    }

    public double getEnableFund() {
        return enableFund;
    }

    public void setEnableFund(double enableFund) {
        this.enableFund = enableFund;
    }

    public double getFetchFund() {
        return fetchFund;
    }

    public void setFetchFund(double fetchFund) {
        this.fetchFund = fetchFund;
    }
}
