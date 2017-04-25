package com.sbai.finance.model;

/**
 * Created by lixiaokuan0819 on 2017/4/17.
 */

public class EconomicCircle {
    int type;

    public EconomicCircle(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EconomicCircle{" +
                "type=" + type +
                '}';
    }
}
