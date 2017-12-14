package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.KlineBattle;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;

import java.util.List;

/**
 * k线单人练习
 */

public class KlineBattlePkActivity extends KlineBattleDetailActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void buyOperate() {
    }

    @Override
    protected void clearOperate() {
    }

    @Override
    protected void passOperate() {
    }


    @Override
    protected void battleFinish() {
        super.battleFinish();
        if (mKlineBattle == null) return;
        Launcher.with(getActivity(), KlinePracticeResultActivity.class)
                .putExtra(ExtraKeys.BATTLE_STOCK_START_TIME, mKlineBattle.getBattleStockStartTime())
                .putExtra(ExtraKeys.BATTLE_STOCK_END_TIME, mKlineBattle.getBattleStockEndTime())
                .putExtra(ExtraKeys.BATTLE_STOCK_CODE, mKlineBattle.getBattleVarietyCode())
                .putExtra(ExtraKeys.BATTLE_STOCK_NAME, mKlineBattle.getBattleVarietyName())
                .putExtra(ExtraKeys.BATTLE_PROFIT, mOperateView.getTotalProfit())
                .execute();
    }
}
