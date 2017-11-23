package com.sbai.finance.game;

public interface PushCode {

    int BATTLE_CREATED = 11;
    int BATTLE_JOINED = 12; //对战有人加入
    int BATTLE_OVER = 13;
    int BATTLE_CANCEL = 14;

    int ORDER_CREATED = 21;
    int ORDER_CLOSE = 22; //订单平仓

    int QUICK_MATCH_SUCCESS = 31; //快速匹配成功
    int QUICK_MATCH_FAILURE = 32;
    int QUICK_MATCH_TIMEOUT = 33;

    int ROOM_CREATE_TIMEOUT = 41; // 房间创建超时
    int USER_PRAISE  = 51; // 用户点赞
}
