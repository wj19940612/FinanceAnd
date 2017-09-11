package com.sbai.finance.model.fund;

/**
 * Created by ${wangJie} on 2017/6/22.
 * 提现手续费
 */

public class WithPoundage {
    //固定手续费
    private double fee;
    //浮动手续费
    private double floatfee;

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getFloatfee() {
        return floatfee;
    }

    public void setFloatfee(double floatfee) {
        this.floatfee = floatfee;
    }

    @Override
    public String toString() {
        return "WithPoundageModel{" +
                "fee=" + fee +
                ", floatfee=" + floatfee +
                '}';
    }
}
