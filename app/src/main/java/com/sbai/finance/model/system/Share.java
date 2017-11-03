package com.sbai.finance.model.system;

/**
 * Created by ${wangJie} on 2017/10/10.
 * 分享出去的数据  需要从服务端获取
 */

public class Share {

    //活动邀请
    public static final String SHARE_CODE_ACTIVITIES_TO_INVITE = "activity01";
    //训练
    public static final String SHARE_CODE_TRAINING = "train01";
    //问答
    public static final String SHARE_CODE_QUESTION_ANSWER = "question01";
    //操盘对战
    public static final String SHARE_CODE_FUTURE_BATTLE = "game01";
    //乐米日报分享
    public static final String SHARE_CODE_DAILY_REPORT = "class01";
    //竞技场分享
    public static final String SHARE_CODE_ARENA = "stocktrade01";

    /**
     * code : sssssssss1
     * content : 是的实打实
     * createTime : 1507512452000
     * id : 12
     * shareLeUrl : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1507512429776088116.jpg
     * shareLink : www.baidu.com
     * shareModule : 算得上
     * title : 水滴石穿
     * updateTime : 1507512452000
     */

    //代码
    private String code;
    //描述
    private String content;
    private long createTime;
    private int id;
    //小图
    private String shareLeUrl;
    //分享链接
    private String shareLink;
    //分享模块
    private String shareModule;
    //标题
    private String title;
    private long updateTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getShareLeUrl() {
        return shareLeUrl;
    }

    public void setShareLeUrl(String shareLeUrl) {
        this.shareLeUrl = shareLeUrl;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getShareModule() {
        return shareModule;
    }

    public void setShareModule(String shareModule) {
        this.shareModule = shareModule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
