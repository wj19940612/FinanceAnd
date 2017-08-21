package com.sbai.finance.model.training;

/**
 * 提交训练应答
 */

public class TrainingResult {

    private int id;
    private double level;
    private double score;
    private int time;
    private int trainId;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
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
        return "TrainingResult{" +
                "id=" + id +
                ", level=" + level +
                ", score=" + score +
                ", time=" + time +
                ", trainId=" + trainId +
                ", type=" + type +
                '}';
    }
}
