package com.sbai.finance.model.training;

/**
 *  我的训练记录的数据结构，如果当前这个训练我并没有做，那记录那个对象就为空（说明没有记录），只有训练对象
 */
public class MyTrainingRecord {

    private TrainingRecord record;
    private Training train;

    public TrainingRecord getRecord() {
        return record;
    }

    public void setRecord(TrainingRecord record) {
        this.record = record;
    }

    public Training getTrain() {
        return train;
    }

    public void setTrain(Training train) {
        this.train = train;
    }

    @Override
    public String toString() {
        return "MyTrainingRecord{" +
                "record=" + record +
                ", train=" + train +
                '}';
    }
}
