package com.sbai.finance.model.push;

import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.system.JsOpenAppPageType;

/**
 * Created by ${wangJie} on 2017/5/4.
 */

public class PushMessageModel implements JsOpenAppPageType {

//    "msg":"对战匹配成功,赶紧加入吧！","classify":2,"data":"hall","createTime":1499052945482,"title":"对战匹配成功","type":1

    /**
     * classify : 14761
     * createTime : 54111
     * data : {}
     * dataId : 10734
     * iconUrl : 测试内容q9s8
     * msg : 测试内容ln2q
     * title : 测试内容84hj
     * type : 64687
     * url : 测试内容cn15
     */

    private int classify;      // 以前用的classify 和type 双重判断  后面用type 作为唯一表示
    private long createTime;
    private String dataId;
    private String iconUrl;
    private String msg;
    private String title;
    private int type;
    private String url;
    /**
     * data : {"coinType":2,"reward":200,"againstPraise":0,"gameStatus":2,"againstFrom":"hall","batchCode":"HV0SIOYf","launchScore":0,"againstScore":0,"launchPraise":0,"varietyType":"CL","againstUserName":"溺水的鱼","launchUser":154,"launchUserPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1498556534650.png","againstUserPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1498716924314.png","bigVarietyType":"future","endline":60,"modifyTime":1499068428000,"createTime":1499068428000,"varietyId":1,"startTime":1499068438215,"id":538,"varietyName":"美原油","launchUserName":"用户2559","againstUser":130}
     */

    private Battle data;


    public boolean isDailyReportDetail() {
        return getClassify() == DAILY_REPORT && getType() == DAILY_REPORT;
    }


    public boolean isBattleMatchSuccess() {
        return getClassify() == 2 && getType() == 1;
    }

    //小姐姐回复你的问题
    public boolean isMissAnswer() {
        return getClassify() == 4 && getType() == 1;
    }

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Battle getData() {
        return data;
    }

    public void setData(Battle data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PushMessageModel{" +
                "classify=" + classify +
                ", createTime=" + createTime +
                ", dataId='" + dataId + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", msg='" + msg + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", data=" + data +
                '}';
    }
}
