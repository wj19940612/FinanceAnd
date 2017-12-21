package com.sbai.finance.model.klinebattle;

/**
 * K线对决下一条数据
 */

public class BattleKlineOperate extends BattleKlineInfo {
    private BattleKlineData next;


    public BattleKlineData getNext() {
        return next;
    }

    public void setNext(BattleKlineData next) {
        this.next = next;
    }
}
