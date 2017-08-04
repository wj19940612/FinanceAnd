package com.sbai.finance.model.training;

/**
 * Created by ${wangJie} on 2017/8/3.
 * 训练项目model
 */

public class TrainProjectModel {

    private String trainIcon;
    private String trainName;
    private int completeNumber;
    private String completeNeedTime;
    private boolean isTrained;
    private String grade;

    public String getTrainIcon() {
        return trainIcon;
    }

    public void setTrainIcon(String trainIcon) {
        this.trainIcon = trainIcon;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public int getCompleteNumber() {
        return completeNumber;
    }

    public void setCompleteNumber(int completeNumber) {
        this.completeNumber = completeNumber;
    }

    public String getCompleteNeedTime() {
        return completeNeedTime;
    }

    public void setCompleteNeedTime(String completeNeedTime) {
        this.completeNeedTime = completeNeedTime;
    }

    public boolean isTrained() {
        return isTrained;
    }

    public void setTrained(boolean trained) {
        isTrained = trained;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


}
