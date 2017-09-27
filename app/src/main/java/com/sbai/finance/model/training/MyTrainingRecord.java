package com.sbai.finance.model.training;

/**
 * 我的训练记录的数据结构，如果当前这个训练我并没有做，那记录那个对象就为空（说明没有记录），只有训练对象
 */
public class MyTrainingRecord {

    public static final int TYPE_HAS_MORE_TRAINING  = 1;

    private TrainingRecord record;
    private Training train;

    //自己定义的，用来处理还有训练项目的情况
    private int type;   // 0 普通情况  1  还有更多的训练

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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
                ", type=" + type +
                '}';
    }
}
