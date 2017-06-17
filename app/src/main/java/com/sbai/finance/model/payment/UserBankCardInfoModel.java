package com.sbai.finance.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${wangJie} on 2017/6/16.
 * 用户所绑定的银行卡信息
 */

public class UserBankCardInfoModel implements Parcelable {


    /**
     * idStatus : 0
     * realName : 王杰
     * bankId : 1
     * cardPhone : 18182568093
     * issuingBankName : 中国工商银行
     * createTime : 1497664377000
     * idCard : 612522199304024039
     * cardState : 1
     * id : 21
     * userId : 130
     * cardNumber : 6212262604000749165
     * bindStatus : 0
     */

    //账户实名认证状态     0 未认证 1 已填写 3 已认证
    private int idStatus;
    //真实姓名
    private String realName;
    //银行ID
    private int bankId;
    //银行预留手机号
    private String cardPhone;
    //银行名称
    private String issuingBankName;
    private long createTime;
    //生份证号
    private String idCard;
    private int cardState;
    //银行卡id
    private int id;
    private int userId;
    //银行卡号
    private String cardNumber;
    // 银行卡绑定状态  0 绑定  1 解绑  2修改
    private Integer bindStatus;

    public boolean hasBindBank() {
        return getBindStatus() != null && getBindStatus() == 0;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getCardPhone() {
        return cardPhone;
    }

    public void setCardPhone(String cardPhone) {
        this.cardPhone = cardPhone;
    }

    public String getIssuingBankName() {
        return issuingBankName;
    }

    public void setIssuingBankName(String issuingBankName) {
        this.issuingBankName = issuingBankName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getCardState() {
        return cardState;
    }

    public void setCardState(int cardState) {
        this.cardState = cardState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(Integer bindStatus) {
        this.bindStatus = bindStatus;
    }


    @Override
    public String toString() {
        return "UserBankCardInfoModel{" +
                "idStatus=" + idStatus +
                ", realName='" + realName + '\'' +
                ", bankId=" + bankId +
                ", cardPhone='" + cardPhone + '\'' +
                ", issuingBankName='" + issuingBankName + '\'' +
                ", createTime=" + createTime +
                ", idCard='" + idCard + '\'' +
                ", cardState=" + cardState +
                ", id=" + id +
                ", userId=" + userId +
                ", cardNumber='" + cardNumber + '\'' +
                ", bindStatus=" + bindStatus +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idStatus);
        dest.writeString(this.realName);
        dest.writeInt(this.bankId);
        dest.writeString(this.cardPhone);
        dest.writeString(this.issuingBankName);
        dest.writeLong(this.createTime);
        dest.writeString(this.idCard);
        dest.writeInt(this.cardState);
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeString(this.cardNumber);
        dest.writeValue(this.bindStatus);
    }

    public UserBankCardInfoModel() {
    }

    protected UserBankCardInfoModel(Parcel in) {
        this.idStatus = in.readInt();
        this.realName = in.readString();
        this.bankId = in.readInt();
        this.cardPhone = in.readString();
        this.issuingBankName = in.readString();
        this.createTime = in.readLong();
        this.idCard = in.readString();
        this.cardState = in.readInt();
        this.id = in.readInt();
        this.userId = in.readInt();
        this.cardNumber = in.readString();
        this.bindStatus = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<UserBankCardInfoModel> CREATOR = new Creator<UserBankCardInfoModel>() {
        @Override
        public UserBankCardInfoModel createFromParcel(Parcel source) {
            return new UserBankCardInfoModel(source);
        }

        @Override
        public UserBankCardInfoModel[] newArray(int size) {
            return new UserBankCardInfoModel[size];
        }
    };
}
