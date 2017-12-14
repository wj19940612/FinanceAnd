package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.KlineBattle;
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

public class SingleKlineExerciseActivity extends KlineBattleDetailActivity {
    private List<BattleKlineData> mBattleKlineDataList;
    private int mPositionIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestKlineData();
    }

    private void requestKlineData() {
        Client.getSingleKlineBattleData().setTag(TAG)
                .setCallback(new Callback2D<Resp<KlineBattle>, KlineBattle>() {
                    @Override
                    protected void onRespSuccessData(KlineBattle data) {
                        updateExerciseData(data);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void updateExerciseData(KlineBattle data) {
        if (data == null) return;
        mKlineBattle = data;
        mRemainKlineAmount = mKlineBattle.getLine();
        mBattleKlineDataList = mKlineBattle.getUserMarkList();
        if (mBattleKlineDataList != null && mBattleKlineDataList.size() >= 40) {
            mKlineView.initKlineDataList(mBattleKlineDataList.subList(0, 40));
        }
        mCountdown.setTotalTime(200, new KlineBattleCountDownView.OnCountDownListener() {
            @Override
            public void finish() {
                battleFinish();
            }
        });
        setRemainKline();
    }

    @Override
    protected void buyOperate() {
        setKlineViewAndOperateView(KlineBattle.BUY);
    }

    @Override
    protected void clearOperate() {
        setKlineViewAndOperateView(KlineBattle.SELL);
    }

    @Override
    protected void passOperate() {
        setKlineViewAndOperateView(KlineBattle.PASS);
    }

    private void setKlineViewAndOperateView(String type) {
        if (mBattleKlineDataList == null) return;
        if (mCurrentIndex == mBattleKlineDataList.size()) {
            battleFinish();
            return;
        }
        if (mCurrentIndex + 1 < mBattleKlineDataList.size()) {
            BattleKlineData positionKlineData = null;
            BattleKlineData nextKlineData = mBattleKlineDataList.get(mCurrentIndex + 1);
            mKlineView.addKlineData(nextKlineData);
            if (type.equalsIgnoreCase(KlineBattle.PASS)) {
                if (mPositionIndex > -1) {
                    positionKlineData = mBattleKlineDataList.get(mPositionIndex);
                }
            } else {
                positionKlineData = mBattleKlineDataList.get(mCurrentIndex);
            }
            if (positionKlineData != null) {
                mOperateView.setPositionProfit((nextKlineData.getClosePrice() - positionKlineData.getClosePrice()) / positionKlineData.getClosePrice());
            }
            if (type.equalsIgnoreCase(KlineBattle.BUY)) {
                mPositionIndex = mCurrentIndex;
                mOperateView.buySuccess();
            } else if (type.equalsIgnoreCase(KlineBattle.SELL)) {
                mPositionIndex = -1;
                mOperateView.clearSuccess();
            }
            mCurrentIndex = mCurrentIndex + 1;
            mRemainKlineAmount = mRemainKlineAmount - 1;
            setRemainKline();
        }
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
