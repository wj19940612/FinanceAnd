package com.sbai.finance.model.system;

/**
 * Created by ${wangJie} on 2017/11/2.
 * h5和原生页面交互的type
 * <p>
 * //0 日报 1用户相关的信息,3 学一学, 4关注小姐姐用户的推送-后台,5训练 6活动 7模块 8反馈 9关注小姐姐用户的推送-前台自动 10 期货对战 11 竞技场 12 充值 13钱包
 */

public interface JsOpenAppPageType {
    int DAILY_REPORT = 0; //日报
    int USER_ABOUT_INFO = 1;//用户相关信息  暂时不用
    int SELF_STUDY_ROOM = 3; //自习室
    int QUESTION_DETAIL = 4; //问题详情页
    int TRAINING = 5;      //训练页
    int ACTIVITY = 6;      //活动
    int MODULE = 7;        //模块
    int FEED_BACK_REPLY = 8; //意见反馈
    int MISS_MESSAGE = 9;   //小姐姐资料页
    int FUTURE_BATTLE = 10; //期货对战啊
    int ARENA = 11;         //竞技场
    int RECHARGE = 12;      // 充值  默认充值方式为元宝 如果有需要 可以在AppJs 的jsOpenAppPage 中的data 增加标志位
    int WALLET = 13;        // 钱包
}
