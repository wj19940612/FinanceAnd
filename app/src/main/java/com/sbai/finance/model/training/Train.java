package com.sbai.finance.model.training;

/**
 * 训练
 */
public class Train {

    public static final int TRAIN_THEORY = 1;
    public static final int TRAIN_TECHNOLOGY = 2;
    public static final int TRAIN_BASIS = 3;
    public static final int TRAIN_COMPREHENSIVE = 4;

    /**
     * train : {"createTime":"2017-08-01 16:49:57","finishCount":0,"id":4,"imageUrl":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1501577017042089500.png","knowledgeUrl":"https://www.baidu.com","level":2,"modifyTime":"2017-08-08 15:24:57","recommend":1,"status":1,"time":120,"title":"训练新增（1）测试"}
     */

    private TrainProgram train;

    public TrainProgram getTrain() {
        return train;
    }

    public void setTrain(TrainProgram train) {
        this.train = train;
    }

}
