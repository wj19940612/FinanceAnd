package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.model.ImageFloder;
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

public class SingleKlineExerciseActivity extends BattleKlineDetailActivity {
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
        mBattleUserMarkList = mBattleKline.getUserMarkList();
        if (mBattleUserMarkList != null && mBattleUserMarkList.size() >= 40) {
            mRemainKlineAmount = mBattleKline.getUserMarkList().size() - 40;
            List<BattleKlineData> subList = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                subList.add(mBattleUserMarkList.get(i));
            }
            mKlineView.initKlineDataList(subList);
        }
        if (mBattleKline.getEndTime() == 0) {
            mBattleKline.setEndTime(SysTime.getSysTime().getSystemTimestamp() + 2 * 60 * 1000);
        }
        long totalTime = ((mBattleKline.getEndTime() - SysTime.getSysTime().getSystemTimestamp()));
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
            if (type.equalsIgnoreCase(BattleKline.PASS)) {
                if (mPositionIndex > -1) {
                    positionKlineData = mBattleUserMarkList.get(mPositionIndex);
                }
            } else {
                positionKlineData = mBattleUserMarkList.get(mCurrentIndex);
            }
            if (type.equalsIgnoreCase(BattleKline.BUY)) {
                mPositionIndex = mCurrentIndex;
                mKlineView.getLastData().setMark(BattleKlineData.MARK_BUY);
                mOperateView.buySuccess();
            } else if (type.equalsIgnoreCase(BattleKline.SELL)) {
                mKlineView.getLastData().setMark(BattleKlineData.MARK_SELL);
                mPositionIndex = -1;
                mOperateView.clearSuccess();
            } else {
                if (mPositionIndex > -1) {
                    mKlineView.getLastData().setMark(BattleKlineData.MARK_HOLD_PASS);
                } else {
                    mKlineView.getLastData().setMark(BattleKlineData.MARK_PASS);
                }
            }
            if (positionKlineData != null) {
                double positionProfit = (nextKlineData.getClosePrice() - positionKlineData.getClosePrice()) / positionKlineData.getClosePrice();
                mOperateView.setTotalProfit(positionProfit - mOperateView.getLastPosition() + mOperateView.getTotalProfit());
                if (mPositionIndex > -1) {
                    nextKlineData.setPositions(positionProfit);
                    mOperateView.setPositionProfit(positionProfit);
                }
            }
            if (mCurrentIndex == mBattleUserMarkList.size() - 2) {
                mKlineView.setLastInvisibleData(mBattleUserMarkList.get(mBattleUserMarkList.size() - 1));
                battleFinish();
                return;
            }
            mKlineView.addKlineData(nextKlineData);
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
                .putExtra(ExtraKeys.BATTLE_KLINE_DATA, mBattleKline)
                .putExtra(ExtraKeys.BATTLE_PROFIT, mOperateView.getTotalProfit())
                .execute();
        finish();
    }
}