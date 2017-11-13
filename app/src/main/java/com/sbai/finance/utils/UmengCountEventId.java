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
     * me0200,修改用户信息,0
     * me0100,头像,0
     * me0400,登录,0
     * me0600,昵称,0
     * me0601,性别,0
     * me0602,年龄,0
     * me0603,地区,0
     * me0700,信用,0
     * me0701,实名认证,0
     * me0207,绑定微信,0
     * me0208,退出登录,0
     * me0300,查看学分,0
     * me0401,钱包,0
     * me0500,我的问答,0
     * me0501,我的问答_提问,0
     * me0502,我的问答_评论,0
     * me1600,我的收藏,0
     * me1601,我的收藏_提问,0
     * me1602,我的收藏_文章,0
     * me0702,消息0
     * me0703,消息_回复,0
     * me0704,消息_系统,0
     * me0800,意见反馈,0
     * me0802,金融测评_开始测试,0
     * me0803,金融测评_历史结果,0
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
    public static final String ME_BIND_WECHAT = "me0207";
    public static final String ME_EXIT_LOGIN = "me0208";
    public static final String ME_MOD_USER_INFO = "me0200";
    public static final String ME_SEE_MY_CREDIT = "me0300";
    public static final String ME_WALLET = "me0401";
    public static final String ME_MY_QUESTION = "me0500";
    public static final String ME_MY_QUESTION_ASK = "me0501";
    public static final String ME_MY_QUESTION_COMMENT = "me0502";
    public static final String ME_MY_COLLECTION = "me1600";
    public static final String ME_MY_COLLECTION_ASK = "me1601";
    public static final String ME_MY_COLLECTION_ARTICLE = "me0602";
    public static final String ME_NEWS = "me0702";
    public static final String ME_NEWS_REPLY = "me0702";
    public static final String ME_NEWS_SYS = "me0702";

    public static final String ME_AVATAR = "me0100";
    public static final String ME_LOGIN = "me0400";
    public static final String ME_NICK_NAME = "me0600";
    public static final String ME_SEX = "me0601";
    public static final String ME_AGE = "me0602";
    public static final String ME_LOCATION = "me0603";
    public static final String ME_CREDIT = "me0700";
    public static final String ME_CERTIFICATION = "me0701";
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


    public static final String DISCOVERY_ADD_SELF_OPTIONAL = "find0101";

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


    /**
     * 首页
     * page0100,沪深,0
     * page0101,沪深_查看全部,0
     * page0200,期货,0
     * page0201,期货_查看全部,0
     * page0300,自选,0
     * page0301,自选_查看全部,0
     * page0400,广播,0
     * page0500,练一练,0
     * page0600,一日一题,0
     * page0700,banner,0
     * page0701,运营位1,0
     * page0702,运营位2,0
     * page0703,运营位3,0
     * find0500,土豪榜,0
     * find0600,盈利榜,0
     * find0700,学霸榜,0
     * page0901,快讯_更多,0
     * page1000,要闻_更多,0
     * page1001,要闻收藏,0
     * page1002,要闻分享_朋友圈,0
     * page1003,要闻分享_微信好友,0
     * page1004,要闻分享_微博,0
     */
    public static final String PAGE_HU_SHEN = "page0100";
    public static final String PAGE_HU_SHEN_ALL = "page0101";
    public static final String PAGE_FUTURE = "page0200";
    public static final String PAGE_FUTURE_ALL = "page0201";
    public static final String PAGE_OPTIONAL = "page0300";
    public static final String PAGE_OPTIONAL_ALL = "page0301";
    public static final String PAGE_BROADCAST = "page0400";
    public static final String PAGE_TRAINING = "page0500";
    public static final String PAGE_STUDY_ROOM = "page0600";
    public static final String PAGE_BANNER = "page0700";
    public static final String PAGE_OPERATE_1 = "page0701";
    public static final String PAGE_OPERATE_2 = "page0702";
    public static final String PAGE_OPERATE_3 = "page0703";
    public static final String FIND_INGOT = "find0500";
    public static final String FIND_PROFIT = "find0600";
    public static final String FIND_SAVANT = "find0700";
    public static final String PAGE_INFORMATION_MORE = "page0901";
    public static final String PAGE_FOCUS_NEWS_MORE = "page1000";
    public static final String PAGE_FOCUS_NEWS_COLLECT = "page1001";
    public static final String PAGE_FOCUS_NEWS_SHARE_CIRCLE = "page1002";
    public static final String PAGE_FOCUS_NEWS_SHARE_FRIEND = "page1003";
    public static final String PAGE_FOCUS_NEWS_SHARE_SINA = "page1004";

    /**
     * 行情
     * find0100,标签_自选,0
     * find0101,添加自选,0
     * <p>
     * find0200,标签_股票,0
     * find0201,新闻,0
     * find0202,财务,0
     * find0203,股票分享,0
     * find0204,股票分享_微信好友,0
     * find0205,股票分享_微博,0
     * find0206,股票分享_朋友圈,0
     * <p>
     * find0300,标签_期货,0
     * find0301,交易,0
     * find0303,期货分享,0
     * find0304,期货分享_微信好友,0
     * find0305,期货分享_微博,0
     * find0306,期货分享_朋友圈,0
     */
    public static final String FIND_OPTIONAL = "find0100";
    public static final String FIND_OPTIONAL_ADD = "find0101";
    public static final String FIND_STOCK = "find0200";
    public static final String FIND_STOCK_NEWS = "find0201";
    public static final String FIND_STOCK_FINANCE = "find0202";
    public static final String FIND_STOCK_SHARE = "find0203";
    public static final String FIND_STOCK_SHARE_FRIEND = "find0204";
    public static final String FIND_STOCK_SHARE_SINA = "find0205";
    public static final String FIND_STOCK_SHARE_CIRCLE = "find0206";
    public static final String FIND_FUTURE = "find0300";
    public static final String FIND_FUTURE_TRADE = "find0301";
    public static final String FIND_FUTURE_SHARE = "find0303";
    public static final String FIND_FUTURE_SHARE_FRIEND = "find0304";
    public static final String FIND_FUTURE_SHARE_SINA = "find0305";
    public static final String FIND_FUTURE_SHARE_CIRCLE = "find0306";

    /**
     * 竞技场_首页
     * arena0100,竞技场tap,0
     * arena0200,我的元宝,0
     * arena0300,知识点,0
     * arena0400,赏金赛,0
     * arena0500,普通场,0
     */
    public static final String ARENA_TAP = "arena0100";
    public static final String ARENA_INGOT = "arena0200";
    public static final String ARENA_KNOWLEDGE = "arena0300";
    public static final String ARENA_MRPK = "arena0400";
    public static final String ARENA_FUTURE_PK = "arena0500";
    /**
     * 竞技场_赏金赛
     * mrpk0100,赏金赛_充值,0
     * mrpk0200,赏金赛_我的战绩,0
     * mrpk0300,赏金赛_活动规则,0
     * mrpk0400,赏金赛_排行榜,0
     * mrpk0500,赏金赛_快速匹配,0
     * mrpk0600,赏金赛_当前对战,0
     * mrpk0900,邀请好友_微信好友,0
     * mrpk1000,邀请好友_朋友圈,0
     * mrpk1001,赏金赛对战中_买多,0
     * mrpk1002,赏金赛对战中_卖空,0
     * mrpk1003,赏金赛对战中_平仓,0
     * mrpk1004,赏金赛对战中_用户头像,0
     * mrpk1005,赏金赛观战_点赞,0
     */
    public static final String MRPK_RECHARAGE = "mrpk0100";
    public static final String MRPK_MY_RECORD = "mrpk0200";
    public static final String MRPK_RULE = "mrpk0300";
    public static final String MRPK_LEARD_BOARD = "mrpk0400";
    public static final String MRPK_FAST_MATCH = "mrpk0500";
    public static final String MRPK_CURRENT_BATTLE = "mrpk0600";
    public static final String MRPK_INVITE_FRIEND = "mrpk0900";
    public static final String MRPK_INVITE_CIRCLE = "mrpk1000";
    public static final String MRPK_BUY = "mrpk1001";
    public static final String MRPK_SELL = "mrpk1002";
    public static final String MRPK_CLEAR = "mrpk1003";
    public static final String MRPK_USER_AVATAR = "mrpk1004";
    public static final String MRPK_PRAISE = "mrpk1005";

    /**
     * 竞技场_普通场
     * futurespk0100,对战大厅_充值,0
     * futurespk0200,对战大厅_我的战绩,0
     * futurespk0300,对战大厅_对战规则,0
     * futurespk0400,对战大厅_匹配对战,0
     * futurespk0500,对战大厅_创建对战,0
     * futurespk0600,创建对战_发起对战,0
     * futurespk0700,对战大厅_当前对战,0
     * futurespk0900,等待房间_快速匹配,0
     * futurespk1000,等待房间_取消对战,0
     * futurespk1001,对战中_买多,0
     * futurespk1002,对战中_卖空,0
     * futurespk1003,对战中_平仓,0
     * futurespk1004,对战中_用户头像,0
     * futurespk1005,观战_点赞,0
     */

    public static final String FUTURE_PK_RECHARGE = "futurespk0100";
    public static final String FUTURE_PK_MY_RECORD = "futurespk0200";
    public static final String FUTURE_PK_RULE = "futurespk0300";
    public static final String FUTURE_PK_MATCH = "futurespk0400";
    public static final String FUTURE_PK_CREATE = "futurespk0500";
    public static final String FUTURE_PK_LAUNCHER = "futurespk0600";
    public static final String FUTURE_PK_CURRENT = "futurespk0700";
    public static final String FUTURE_PK_FAST_MATCH = "futurespk0900";
    public static final String FUTURE_PK_CANCEL_MATCH = "futurespk1000";
    public static final String FUTURE_PK_BUY = "futurespk1001";
    public static final String FUTURE_PK_SELL = "futurespk1002";
    public static final String FUTURE_PK_CLEAR = "futurespk1003";
    public static final String FUTURE_PK_USER_AVATAR = "futurespk1004";
    public static final String FUTURE_PK_PRAISE = "futurespk1005";

    /**
     * 我的_钱包
     * purse0100,元宝,0
     * purse0101,元宝_充值,0
     * purse0102,元宝充值_规则,0
     * purse0103,元宝充值_联系客服,0
     * purse0200,现金,0
     * purse0201,现金_充值,0
     * purse0202,现金充值_联系客服,0
     */
    public static final String WALLET_INGOT = "purse0100";
    public static final String WALLET_INGOT_RECHARGE = "purse0101";
    public static final String WALLET_INGOT_RULE = "purse0102";
    public static final String WALLET_INGOT_CALL_SERVICE = "purse0103";
    public static final String WALLET_CASH = "purse0200";
    public static final String WALLET_CASH_RECHARGE = "purse0201";
    public static final String WALLET_CASH_CALL_SERVICE = "purse0202";
}

