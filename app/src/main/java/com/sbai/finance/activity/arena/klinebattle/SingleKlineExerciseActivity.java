package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;

import java.util.ArrayList;
import java.util.List;

/**
 * k线单人练习
 */

public class SingleKlineExerciseActivity extends KlineBattleDetailActivity {
    private List<BattleKlineData> mBattleUserMarkList;
    private int mPositionIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestKlineData();
    }

    private void requestKlineData() {
        Client.getSingleKlineBattleData().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKline>, BattleKline>() {
                    @Override
                    protected void onRespSuccessData(BattleKline data) {
                        updateExerciseData(data);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void updateExerciseData(BattleKline data) {
        if (data == null) return;
        mBattleKline = data;
        mRemainKlineAmount = mBattleKline.getLine();
        mBattleUserMarkList = mBattleKline.getUserMarkList();
        if (mBattleUserMarkList != null && mBattleUserMarkList.size() >= 40) {
            List<BattleKlineData> subList = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                subList.add(mBattleUserMarkList.get(i));
            }
            mKlineView.initKlineDataList(subList);
        }
        int totalTime = (int) ((mBattleKline.getEndTime() - SysTime.getSysTime().getSystemTimestamp()) / 1000);
        mCountdown.setTotalTime(totalTime, new KlineBattleCountDownView.OnCountDownListener() {
            @Override
            public void finish() {
                battleFinish();
            }
        });
        setRemainKline();
    }

    @Override
    protected void buyOperate() {
        setKlineViewAndOperateView(BattleKline.BUY);
    }

    @Override
    protected void clearOperate() {
        setKlineViewAndOperateView(BattleKline.SELL);
    }

    @Override
    protected void passOperate() {
        setKlineViewAndOperateView(BattleKline.PASS);
    }

    private void setKlineViewAndOperateView(String type) {
        if (mBattleUserMarkList == null) return;
        if (mCurrentIndex + 1 < mBattleUserMarkList.size()) {
            BattleKlineData positionKlineData = null;
            BattleKlineData nextKlineData = mBattleUserMarkList.get(mCurrentIndex + 1);
            mKlineView.addKlineData(nextKlineData);
            if (type.equalsIgnoreCase(BattleKline.PASS)) {
                if (mPositionIndex > -1) {
                    positionKlineData = mBattleUserMarkList.get(mPositionIndex);
                }
            } else {
                positionKlineData = mBattleUserMarkList.get(mCurrentIndex);
            }
            if (positionKlineData != null) {
                double positionProfit = (nextKlineData.getClosePrice() - positionKlineData.getClosePrice()) / positionKlineData.getClosePrice();
                mOperateView.setPositionProfit(positionProfit);
                mOperateView.setTotalProfit(positionProfit + mOperateView.getTotalProfit());
            }
            if (type.equalsIgnoreCase(BattleKline.BUY)) {
                mPositionIndex = mCurrentIndex;
                mOperateView.buySuccess();
            } else if (type.equalsIgnoreCase(BattleKline.SELL)) {
                mPositionIndex = -1;
                mOperateView.clearSuccess();
            }
            if (mCurrentIndex == mBattleUserMarkList.size() - 2) {
                battleFinish();
                return;
            }
            mCurrentIndex = mCurrentIndex + 1;
            mRemainKlineAmount = mRemainKlineAmount - 1;
            setRemainKline();
        }
    }

    @Override
    protected void battleFinish() {
        super.battleFinish();
        if (mBattleKline == null) return;
        Launcher.with(getActivity(), KlinePracticeResultActivity.class)
                .putExtra(ExtraKeys.BATTLE_STOCK_START_TIME, mBattleKline.getBattleStockStartTime())
                .putExtra(ExtraKeys.BATTLE_STOCK_END_TIME, mBattleKline.getBattleStockEndTime())
                .putExtra(ExtraKeys.BATTLE_STOCK_CODE, mBattleKline.getBattleVarietyCode())
                .putExtra(ExtraKeys.BATTLE_STOCK_NAME, mBattleKline.getBattleVarietyName())
                .putExtra(ExtraKeys.BATTLE_PROFIT, mOperateView.getTotalProfit())
                .execute();
        finish();
    }
}
