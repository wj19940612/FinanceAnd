package com.sbai.finance.model.training;

/**
 * Created by ${wangJie} on 2017/8/8.
 */

public class TrainAppraiseAndRemark {

    /**
     * createTime : 2017-07-26 16:45:15
     * id : 1
     * modifyTime : 2017-07-26 16:45:15
     * remark : test
     * socreEnd : 200
     * socreStart : 0
     * status : 0
     */

    private String createTime;
    private int id;
    private String modifyTime;
    //评价建议内容
    private String remark;
    private int socreEnd;
    private int socreStart;
    private int status;
    //评价
    private String appraise;

    public String getAppraise() {
        return appraise;
    }

    public void setAppraise(String appraise) {
        this.appraise = appraise;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getSocreEnd() {
        return socreEnd;
    }

    public void setSocreEnd(int socreEnd) {
        this.socreEnd = socreEnd;
    }

    public int getSocreStart() {
        return socreStart;
    }

    public void setSocreStart(int socreStart) {
        this.socreStart = socreStart;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TrainAppraiseAndRemark{" +
                "createTime='" + createTime + '\'' +
                ", id=" + id +
                ", modifyTime='" + modifyTime + '\'' +
                ", remark='" + remark + '\'' +
                ", socreEnd=" + socreEnd +
                ", socreStart=" + socreStart +
                ", status=" + status +
                ", appraise='" + appraise + '\'' +
                '}';
    }
}
