package com.sbai.finance.model.anchor;

import java.util.List;

/**
 * Created by houcc on 2017-08-01.
 */

public class RewardInfo {
    public static final int TYPE_MISS=1;
    public static final int TYPE_QUESTION=2;
    private int id;
    private int index;
    private int type;
    private long money;
    private List<RewardMoney> mMoneyList;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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
