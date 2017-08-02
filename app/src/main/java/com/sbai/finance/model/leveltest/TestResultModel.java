package com.sbai.finance.model.leveltest;

/**
 * Created by ${wangJie} on 2017/8/2.
 * 测评结果
 */

public class TestResultModel {

    private long createTime;
    //等级
    private int grade;
    //正确率
    private String accuracy;

    public TestResultModel(long createTime, int grade, String accuracy) {
        this.createTime = createTime;
        this.grade = grade;
        this.accuracy = accuracy;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
