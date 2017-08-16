package com.sbai.finance.model.training;

/**
 * 用户训练记录的数据结构：由参与训练过的用户和训练组成
 */
public class TrainedUserRecord {

    private TrainingUser user;
    private TrainingRecord record;

    public TrainingUser getUser() {
        return user;
    }

    public void setUser(TrainingUser user) {
        this.user = user;
    }

    public TrainingRecord getRecord() {
        return record;
    }

    public void setRecord(TrainingRecord record) {
        this.record = record;
    }

    @Override
    public String toString() {
        return "TrainedUserRecord{" +
                "user=" + user +
                ", record=" + record +
                '}';
    }
}
