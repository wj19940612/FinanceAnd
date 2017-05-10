package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/5/9.
 * 用户实名信息
 */

public class UserIdentityCardInfo {

    /**
     * auditeDate : 84457
     * auditer : 测试内容3oa6
     * certBack : 测试内容j616
     * certCode : 测试内容v87k
     * certPositive : 测试内容xc3f
     * createDate : 12004
     * realName : 测试内容1w34
     * status : 38570
     */

    //审核时间
    private int auditeDate;
    //审核人
    private String auditer;
    //反面
    private String certBack;
    //身份证号
    private String certCode;
    //正面
    private String certPositive;
    //申请时间
    private long createDate;
    private String realName;
    //	待审核0、审核通过1、审核未通过 2
    private int status;

    public int getAuditeDate() {
        return auditeDate;
    }

    public void setAuditeDate(int auditeDate) {
        this.auditeDate = auditeDate;
    }

    public String getAuditer() {
        return auditer;
    }

    public void setAuditer(String auditer) {
        this.auditer = auditer;
    }

    public String getCertBack() {
        return certBack;
    }

    public void setCertBack(String certBack) {
        this.certBack = certBack;
    }

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    public String getCertPositive() {
        return certPositive;
    }

    public void setCertPositive(String certPositive) {
        this.certPositive = certPositive;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserIdentityCardInfo{" +
                "auditeDate=" + auditeDate +
                ", auditer='" + auditer + '\'' +
                ", certBack='" + certBack + '\'' +
                ", certCode='" + certCode + '\'' +
                ", certPositive='" + certPositive + '\'' +
                ", createDate=" + createDate +
                ", realName='" + realName + '\'' +
                ", status=" + status +
                '}';
    }
}
