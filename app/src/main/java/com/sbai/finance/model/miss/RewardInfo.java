package com.sbai.finance.model.miss;

import java.util.List;

/**
 * Created by houcc on 2017-08-01.
 */

public class RewardInfo {
    private int id;
    private int index;
    private long money;
    private List<RewardMoney> mMoneyList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public List<RewardMoney> getMoneyList() {
        return mMoneyList;
    }

    public void setMoneyList(List<RewardMoney> moneyList) {
        mMoneyList = moneyList;
    }
}
