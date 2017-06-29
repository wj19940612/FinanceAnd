package com.sbai.finance.utils;

/**
 * Created by lixiaokuan0819 on 2017/6/29.
 */

public class UmengCountEventIdUtils {

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
	public static final String WAITTING_ROOM_INVITE_FRIENDS = "futurespk0800";
	//等待房间_快速匹配
	public static final String WAITTING_ROOM_FAST_MATCH = "futurespk0900";
	//等待房间_取消对战
	public static final String WAITTING_ROOM_CANCEL_BATTLE = "futurespk1000";
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
	public static final String VIRTUSL_WALLET_INGOT_DETAILS = "virtualwallet0100";
	//聚宝盆_积分明细
	public static final String VIRTUSL_WALLET_INTEGRAL_DETAILS = "virtualwallet0200";
	//聚宝盆_兑换规则
	public static final String VIRTUSL_WALLET_EXCHANGE_RULES = "virtualwallet0300";
	//聚宝盆_购买元宝
	public static final String VIRTUSL_WALLET_BUY_INGOT = "virtualwallet0400";
	//聚宝盆_兑换积分
	public static final String VIRTUSL_WALLET_EXCHANGE_INTEGRAL = "virtualwallet0500";
	//聚宝盆_购买兑换确认弹窗_取消
	public static final String VIRTUSL_WALLET_POPUP_WINDOW_CANCEL = "virtualwallet0600";
	//聚宝盆_购买兑换确认弹窗_确定
	public static final String VIRTUSL_WALLET_POPUP_WINDOW_CONFIRM = "virtualwallet0700";
}
