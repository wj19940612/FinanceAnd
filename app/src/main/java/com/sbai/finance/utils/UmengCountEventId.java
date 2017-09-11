package com.sbai.finance.utils;

/**
 * 友盟事件统计 id
 */

public class UmengCountEventId {

    /**
     * 期货对战
     * futurespk0100,对战大厅_充值,0
     * futurespk0200,对战大厅_查看战绩,0
     * futurespk0300,对战大厅_决斗规则,0
     * futurespk0400,对战大厅_匹配对战,0
     * futurespk0500,对战大厅_创建对战,0
     * futurespk0600,创建对战_发起对战,0
     * futurespk0700,对战大厅_当前对战,0
     * futurespk0800,等待房间_邀请好友,0
     * futurespk0900,等待房间_快速匹配,0
     * futurespk1000,等待房间_取消对战,0
     * futurespk1001,对战中_买多,0
     * futurespk1002,对战中_卖空,0
     * futurespk1003,对战中_平仓,0
     * futurespk1004,对战中_用户头像,0
     * futurespk1005,观战_点赞,0
     */

    //对战大厅_充值
    public static final String BATTLE_HALL_RECHARGE = "futurespk0100";
    //对战大厅_查看战绩
    public static final String BATTLE_HALL_CHECK_RECODE = "futurespk0200";
    //对战大厅_决斗规则
    public static final String BATTLE_HALL_DUEL_RULES = "futurespk0300";
    //对战大厅_匹配对战
    public static final String BATTLE_HALL_MATCH_BATTLE = "futurespk0400";
    //对战大厅_创建对战
    public static final String BATTLE_HALL_CREATE_BATTLE = "futurespk0500";
    //对战大厅_发起对战
    public static final String BATTLE_HALL_LAUNCH_BATTLE = "futurespk0600";
    //对战大厅_当前对战
    public static final String BATTLE_HALL_CURRENT_BATTLE = "futurespk0700";
    //等待房间_邀请好友
    public static final String WAITING_ROOM_INVITE_FRIENDS = "futurespk0800";
    //等待房间_快速匹配
    public static final String WAITING_ROOM_FAST_MATCH = "futurespk0900";
    //等待房间_取消对战
    public static final String WAITING_ROOM_CANCEL_BATTLE = "futurespk1000";
    //对战中_买多
    public static final String BATTLE_BULLISH = "futurespk1001";
    //对战中_卖空
    public static final String BATTLE_BEARISH = "futurespk1002";
    //对战中_平仓
    public static final String BATTLE_CLOSE_POSITION = "futurespk1003";
    //对战中_用户头像
    public static final String BATTLE_USER_AVATAR = "futurespk1004";
    //观战_点赞
    public static final String WITNESS_BATTLE_PRAISE = "futurespk1005";

    /**
     * 钱包
     * wallet0100,钱包_充值,0
     * wallet0200,钱包_提现,0
     * wallet0300,钱包_银行卡,0
     * wallet0400,钱包_明细,0
     * wallet0500,充值_下一步,0
     * wallet0600,充值_联系客服,0
     * wallet0700,银行卡充值确认页面_获取验证码,0
     * wallet0800,银行卡充值确认页面_确认充值,0
     * wallet0900,提现_提现规则,0
     * wallet1000,提现_下一步,0
     * wallet1001,提现_联系客服,0
     * wallet1002,银行卡填写_下一步,0
     * wallet1003,银行卡列表_解绑,0
     */

    //钱包_充值
    public static final String WALLET_RECHARGE = "wallet0100";
    //钱包_提现
    public static final String WALLET_WITHDRAW = "wallet0200";
    //钱包_银行卡
    public static final String WALLET_BANK_CARD = "wallet0300";
    //钱包_明细
    public static final String WALLET_DETAILS = "wallet0400";
    //充值_下一步
    public static final String RECHARGE_NEXT_STEP = "wallet0500";
    //充值_联系客服
    public static final String RECHARGE_CONTACT_CUSTOMER_SERVICE = "wallet0600";
    //银行卡充值确认页面_获取验证码
    public static final String GET_VERIFICATION_CODE = "wallet0700";
    //银行卡充值确认页面_确认充值
    public static final String CONFIRM_RECHARGE = "wallet0800";
    //提现_提现规则
    public static final String WITHDRAW_RULES = "wallet0900";
    //提现_下一步
    public static final String WITHDRAW_NEXT_STEP = "wallet1000";
    //提现_联系客服
    public static final String WITHDRAW_CONTACT_CUSTOMER_SERVICE = "wallet1001";
    //银行卡填写_下一步
    public static final String BANK_CARD_FILL_NEXT_STEP = "wallet1002";
    //银行卡列表_解绑
    public static final String BANK_CARD_LIST_UNBUNDLED = "wallet1003";

    /**
     * 聚宝盆
     * virtualwallet0100,聚宝盆_元宝明细,0
     * virtualwallet0200,聚宝盆_积分明细,0
     * virtualwallet0300,聚宝盆_兑换规则,0
     * virtualwallet0400,聚宝盆_购买元宝,0
     * virtualwallet0500,聚宝盆_兑换积分,0
     * virtualwallet0600,聚宝盆_购买兑换确认弹窗_取消,0
     * virtualwallet0700,聚宝盆_购买兑换确认弹窗_确定,0
     */

    //聚宝盆_元宝明细
    public static final String WALLET_INGOT_DETAILS = "virtualwallet0100";
    //聚宝盆_积分明细
    public static final String WALLET_INTEGRAL_DETAILS = "virtualwallet0200";
    //聚宝盆_兑换规则
    public static final String WALLET_EXCHANGE_RULES = "virtualwallet0300";
    //聚宝盆_购买元宝
    public static final String WALLET_BUY_INGOT = "virtualwallet0400";
    //聚宝盆_兑换积分
    public static final String WALLET_EXCHANGE_INTEGRAL = "virtualwallet0500";
    //聚宝盆_购买兑换确认弹窗_取消
    public static final String WALLET_POPUP_WINDOW_CANCEL = "virtualwallet0600";
    //聚宝盆_购买兑换确认弹窗_确定
    public static final String WALLET_POPUP_WINDOW_CONFIRM = "virtualwallet0700";
    /**
     * 我的
     * me0100,头像,0
     * me0400,登录,0
     * me0600,昵称,0
     * me0601,性别,0
     * me0602,年龄,0
     * me0603,地区,0
     * me0700,信用,0
     * me0701,实名认证,0
     * me0702,消息0
     * me0800,意见反馈,0
     * me0801,金融水平初步测评,0
     * me0802,开始测试,0
     * me0803,历史结果,0
     * me0804,设置,0
     * me0900,安全中心,0
     * me0901,设置登录密码,0
     * me1000,设置安全密码,0
     * me1001,修改登录密码,0
     * me1002,修改安全密码,0
     * me1100,忘记安全密码,0
     * me1101,新消息通知,0
     * me1200,关于我们,0
     * me1201,功能介绍,0
     * me1202,用户协议,0
     * me1203,检测更新,0
     * me1204,客服微信,0
     * me1205,客服QQ,0
     */
    public static final String ME_AVATAR = "me0100";
    public static final String ME_LOGIN = "me0400";
    public static final String ME_NICK_NAME = "me0600";
    public static final String ME_SEX = "me0601";
    public static final String ME_AGE = "me0602";
    public static final String ME_LOCATION = "me0603";
    public static final String ME_CREDIT = "me0700";
    public static final String ME_CERTIFICATION = "me0701";
    public static final String ME_NEWS = "me0702";
    public static final String ME_FEEDBACK = "me0800";
    public static final String ME_FINANCE_TEST = "me0801";
    public static final String ME_START_TEST = "me0802";
    public static final String ME_TEST_RESULT = "me0803";
    public static final String ME_SETTING = "me0804";
    public static final String ME_SAFETY_CENTER = "me0900";
    public static final String ME_SET_LOGIN_PASSWORD = "me0901";
    public static final String ME_SET_SAFETY_PASSWORD = "me1000";
    public static final String ME_MODIFY_LOGIN_PASSWORD = "me1001";
    public static final String ME_MODIFY_SAFETY_PASSWORD = "me1002";
    public static final String ME_FORGIVE_SAFETY_PASSWORD = "me1100";
    public static final String ME_NEW_AWAIT = "me1101";
    public static final String ME_ABOUT_US = "me1200";
    public static final String ME_FUNCTION_INTRODUCE = "me1201";
    public static final String ME_USER_AGREEMENT = "me1202";
    public static final String ME_CHECK_UPDATE = "me1203";
    public static final String ME_SERVICE_WECHAT = "me1204";
    public static final String ME_SERVICE_QQ = "me1205";

    /**
     * 发现_乐米日报
     * news0100,查看日报列表,0
     * news0200,日报详情详情,0
     * news0300,日报分享,0
     * news0400,日报分享_朋友圈,0
     * news0500,日报分享_微信好友,0
     * news0600,日报分享_微博,0
     */

    public static final String REPORT_VIEW_LIST = "news0100";
    public static final String REPORT_VIEW_DETAIL = "news0200";
    public static final String REPORT_SHARE = "news0300";
    public static final String REPORT_SHARE_CIRCLE = "news0400";
    public static final String REPORT_SHARE_FRIEND = "news0500";
    public static final String REPORT_SHARE_SINA_WEIBO = "news0600";

    /**
     * 发现
     * find0100,自选,0
     * find0101,添加自选,0
     * find0200,股票,0
     * find0201,新闻,0
     * find0202,财务,0
     * find0203,股票分享,0
     * find0204,股票分享_微信好友,0
     * find0205,股票分享_微博,0
     * find0206,股票分享_朋友圈,0
     * find0300,期货,0
     * find0301,交易,0
     * find0303,期货分享,0
     * find0304,期货分享_微信好友,0
     * find0305,期货分享_微博,0
     * find0306,期货分享_朋友圈,0
     * find0400,排行榜,0
     * find0500,土豪榜,0
     * find0600,盈利榜,0
     * find0700,学霸榜,0
     * find0800,你想要学,0
     * find0900,换一批,0
     */
    public static final String DISCOVERY_SELF_OPTIONAL = "find0100";
    public static final String DISCOVERY_ADD_SELF_OPTIONAL = "find0101";
    public static final String DISCOVERY_STOCK = "find0200";
    public static final String DISCOVERY_NEWS = "find0201";
    public static final String DISCOVERY_FINANCE = "find0202";
    public static final String DISCOVERY_SHARE_STOCK = "find0203";
    public static final String DISCOVERY_SHARE_STOCK_FRIEND = "find0204";
    public static final String DISCOVERY_SHARE_STOCK_WEIBO = "find0205";
    public static final String DISCOVERY_SHARE_STOCK_CIRCLE = "find0206";
    public static final String DISCOVERY_FUTURES = "find0300";
    public static final String DISCOVERY_FUTURES_TRADE = "find0301";
    public static final String DISCOVERY_SHARE_FUTURES = "find0303";
    public static final String DISCOVERY_SHARE_FUTURES_FRIEND = "find0304";
    public static final String DISCOVERY_SHARE_FUTURES_WEIBO = "find0305";
    public static final String DISCOVERY_SHARE_FUTURES_CIRCLE = "find0306";
    public static final String DISCOVERY_LEADER_BOARD = "find0400";
    public static final String DISCOVERY_INGOT_BOARD = "find0500";
    public static final String DISCOVERY_PROFIT_BOARD = "find0600";
    public static final String DISCOVERY_SAVANT = "find0700";
    public static final String DISCOVERY_YOU_WANT_LEARN = "find0800";
    public static final String DISCOVERY_CHANGE_BATCH = "find0900";

    /**
     * 姐说
     * sister0100,姐说导航栏,0
     * sister0200,小姐姐头像,0
     * sister0300,问题详情页,0
     * sister0400,语音播放,0
     * sister0500,评论,0
     * sister0600,打赏,0
     * sister0700,关注,0
     * sister0800,点赞,0
     * sister0900,向小姐姐提问,0
     * sister1000,我的提问,0
     * sister1001,回复评论,0
     * sister1002,我的消息,0
     * sister1003,姐说分享,0
     * sister1004,姐说分享_朋友圈,0
     * sister1005,姐说分享_微信好友,0
     * sister1006姐说分享_微博,0
     */
    public static final String MISS_TALK_NAVIGATION = "sister0100";
    public static final String MISS_TALK_AVATAR = "sister0200";
    public static final String MISS_TALK_QUESTION_DETAIL = "sister0300";
    public static final String MISS_TALK_VOICE = "sister0400";
    public static final String MISS_TALK_COMMENT = "sister0500";
    public static final String MISS_TALK_REWARD = "sister0600";
    public static final String MISS_TALK_ATTENTION = "sister0700";
    public static final String MISS_TALK_PRAISE = "sister0800";
    public static final String MISS_TALK_ASK_QUESTION = "sister0900";
    public static final String MISS_TALK_MINE_QUESTION = "sister1000";
    public static final String MISS_TALK_REPLY_COMMENT = "sister1001";
    public static final String MISS_TALK_MINE_MESSAGE = "sister1002";
    public static final String MISS_TALK_SHARE = "sister1003";
    public static final String MISS_TALK_SHARE_CIRCLE = "sister1004";
    public static final String MISS_TALK_SHARE_FRIEND = "sister1005";
    public static final String MISS_TALK_SHARE_WEIBO = "sister1006";

    /**
     * 我的训练
     * home0100,了解学分,0
     * home0200,活动,0
     * home0201,排行榜,0
     * home0202,自习室,0
     * home0300,首页新手测评,0
     */
    public static final String TRAINING_KNOW_CREDITS = "home0100";
    public static final String TRAINING_ACTIVITY = "home0200";
    public static final String TRAINING_LEADER_BOARD = "home0201";
    public static final String TRAINING_STUDY_ROOM = "home0202";
    public static final String TRAINING_TEST = "home0300";
}
