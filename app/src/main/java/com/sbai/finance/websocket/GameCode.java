package com.sbai.finance.websocket;

public interface GameCode {
    int ORDER_NOT_EXISIT = 4610;//订单不存在
    int ORDER_EXISTED = 4611;  //订单已存在
    int ORDER_CLOSED = 4612;  //订单已平仓
    int GAME_OVER = 4607;   //游戏结束
}
