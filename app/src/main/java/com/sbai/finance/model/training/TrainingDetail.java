package com.sbai.finance.model.training;

import java.util.List;

/**
 * 训练详情：由训练对象，训练目标对象
 */
public class TrainingDetail {

    private List<TrainingTarget> targets;
    private Training train;

    public List<TrainingTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<TrainingTarget> targets) {
        this.targets = targets;
    }

    public Training getTrain() {
        return train;
    }

    public void setTrain(Training train) {
        this.train = train;
    }

    @Override
    public String toString() {
        return "TrainingDetail{" +
                "targets=" + targets +
                ", train=" + train +
                '}';
    }
}
