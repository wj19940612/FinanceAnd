package com.sbai.finance.model.training;

/**
 * 训练目标
 */
public class TrainingTarget {

    /**
     * id : 5
     * level : 1
     * rate : 0.5
     * score : 5
     * time : 100
     * trainId : 5
     * type : 1
     */

    private int id;
    private int level;
    private double rate;
    private int score;
    private int time;
    private int trainId;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TrainingTarget{" +
                "id=" + id +
                ", level=" + level +
                ", rate=" + rate +
                ", score=" + score +
                ", time=" + time +
                ", trainId=" + trainId +
                ", type=" + type +
                '}';
    }
}
