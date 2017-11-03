package com.sbai.finance.model.arena;

/**
 * Created by ${wangJie} on 2017/11/1.
 * 用来记录用户的竞技场个人信息
 */

public class UserGameInfo {

    private String receiver;
    private String address;
    private String phone;
    private String wxqq; //微信 qq
    private String skin; //皮肤名称
    private String gameNickName; //游戏昵称
    private String gameZone; //游戏大区


    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWxqq() {
        return wxqq;
    }

    public void setWxqq(String wxqq) {
        this.wxqq = wxqq;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getGameNickName() {
        return gameNickName;
    }

    public void setGameNickName(String gameNickName) {
        this.gameNickName = gameNickName;
    }

    public String getGameZone() {
        return gameZone;
    }

    public void setGameZone(String gameZone) {
        this.gameZone = gameZone;
    }

    @Override
    public String toString() {
        return "UserGameInfo{" +
                "receiver='" + receiver + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", wxqq='" + wxqq + '\'' +
                ", skin='" + skin + '\'' +
                ", gameNickName='" + gameNickName + '\'' +
                ", gameZone='" + gameZone + '\'' +
                '}';
    }
}
