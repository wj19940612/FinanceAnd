package com.sbai.finance.model.payment;

/**
 * Created by lixiaokuan0819 on 2017/5/16.
 */

public class UsablePlatform {

    public static final int TYPE_AIL_PAY = 1;
    public static final int TYPE_WECHAT_PAY = 2;
    public static final int TYPE_BANK_PAY = 3;

    /**
     * createTime : 1494402852000
     * id : 24
     * name : 钱通支付宝
     * payment : 1
     * platform : qtalipay
     * status : 1
     * transfer : 0
     * updateTime : 1494564420000
     */

//	data=[UsablePlatform{createTime=0, id=0, name='支付宝支付', payment=0, platform='qtalipay', status=1, transfer=0, updateTime=0, type=1},
// UsablePlatform{createTime=0, id=0, name='微信支付', payment=0, platform='qtwxscan', status=1, transfer=0, updateTime=0, type=2},
// UsablePlatform{createTime=0, id=0, name='银行卡支付', payment=0, platform='qtbankcardpay', status=1, transfer=0, updateTime=0, type=3}]}


    private long createTime;
    private int id;
    private String name;
    private int payment;
    private String platform;
    private int status;
    private int transfer;
    private long updateTime;
    //1 支付宝
    private int type;

    //目前是支付宝
    public boolean isDeafultPay() {
        return TYPE_AIL_PAY == getType();
    }

    public boolean isBankPay() {
        return getType() == TYPE_BANK_PAY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTransfer() {
        return transfer;
    }

    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UsablePlatform{" +
                "createTime=" + createTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", payment=" + payment +
                ", platform='" + platform + '\'' +
                ", status=" + status +
                ", transfer=" + transfer +
                ", updateTime=" + updateTime +
                ", type=" + type +
                '}';
    }
}
