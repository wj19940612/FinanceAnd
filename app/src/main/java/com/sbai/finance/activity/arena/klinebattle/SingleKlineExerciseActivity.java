package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.KlineBattle;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;

import java.util.List;

/**
 * k线单人练习
 */

public class SingleKlineExerciseActivity extends KlineBattleDetailActivity {
    private List<BattleKlineData> mBattleKlineDataList;

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
            mKlineView.setKlineDataList(mBattleKlineDataList.subList(0, 40));
        }
        mCountdown.setTotalTime(200, new KlineBattleCountDownView.OnCountDownListener() {
            @Override
            public void finish() {
                ToastUtil.show("游戏时间到");
            }
        });
        setRemainKline();
    }

    @Override
    protected void buy() {
        if (mBattleKlineDataList == null) return;
        if (mCurrentIndex == mBattleKlineDataList.size()) return;
        if (mCurrentIndex + 1 < mBattleKlineDataList.size()) {
            BattleKlineData currentKlineData = mBattleKlineDataList.get(mCurrentIndex);
            BattleKlineData nextKlineData = mBattleKlineDataList.get(mCurrentIndex + 1);
            mKlineView.addKlineData(nextKlineData);
            mOperateView.setPositionProfit((nextKlineData.getClosePrice() - currentKlineData.getClosePrice()) / currentKlineData.getClosePrice());
            mOperateView.buySuccess();
            mCurrentIndex += 1;
        }
    }

    @Override
    protected void clear() {
    }

    @Override
    protected void pass() {
    }
}
