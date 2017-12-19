package com.sbai.finance.model;

/**
 * Created by Administrator on 2017\12\19 0019.
 */

public class GuessDealModel {
    private int deal;
    private int guessGameStatus;//猜猜乐是否显示 1-显示 0-不显示

    public int getDeal() {
        return deal;
    }

    public void setDeal(int deal) {
        this.deal = deal;
    }

    public int getGuessGameStatus() {
        return guessGameStatus;
    }

    public void setGuessGameStatus(int guessGameStatus) {
        this.guessGameStatus = guessGameStatus;
    }
}
