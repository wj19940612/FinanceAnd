package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/26.
 */

public class NotReadMessageNumberModel {

    /**
     * classify : 77008
     * count : 85148
     */

    private int classify;
    private int count;

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "NotReadMessageNumberModel{" +
                "classify=" + classify +
                ", count=" + count +
                '}';
    }
}
