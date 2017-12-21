package com.sbai.finance.model.klinebattle;

/**
 * 复盘数据
 */

public class BattleKlineReview extends BattleKline {
    private BattleKlineInfo staInfo;

    public BattleKlineInfo getStaInfo() {
        return staInfo;
    }

    public void setStaInfo(BattleKlineInfo staInfo) {
        this.staInfo = staInfo;
    }
}
