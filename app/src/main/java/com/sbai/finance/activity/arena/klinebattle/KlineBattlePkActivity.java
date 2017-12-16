package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.BattleKlineOperate;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;

import java.util.List;

/**
 * k线Pk练习
 */

public class KlineBattlePkActivity extends KlineBattleDetailActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestBattleInfo();
    }

    private void requestBattleInfo() {
        Client.requestKlineBattleInfo().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKline>, BattleKline>() {
                    @Override
                    protected void onRespSuccessData(BattleKline data) {
                        updateBattleData(data);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void updateBattleData(BattleKline data) {
        mBattleKline = data;
        if (mBattleKline == null) return;
        mKlineView.initKlineDataList(mBattleKline.getUserMarkList());
        updateLastProfitData(mBattleKline.getBattleStaList());
        if (mBattleKline.getUserMarkList() != null) {
            BattleKlineData battleKlineData = mBattleKline.getUserMarkList().get(mBattleKline.getUserMarkList().size() - 1);
            if (battleKlineData.getMark().equalsIgnoreCase(BattleKlineData.MARK_HOLD_PASS)) {
                mOperateView.buySuccess();
                mRemainKlineAmount = data.getLine();
                setRemainKline();
            }
        }
    }

    @Override
    protected void buyOperate() {
        Client.requestKlineBattleBuy().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateLastOperateData(data, BattleKline.BUY);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void updateLastProfitData(List<BattleKline.BattleBean> battleStaList) {
        BattleKline.BattleBean myBattleBean = null;
        for (BattleKline.BattleBean battleBean : battleStaList) {
            if (battleBean.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                myBattleBean = battleBean;
                if (battleBean.getBattleStatus() == BattleKline.STATUS_END) {
                    battleFinish();
                    return;
                }
                if (battleBean.getStatus() == BattleKline.STATUS_END) {
                    mOperateView.complete();
                }
                mOperateView.setRank(battleBean);
            }
        }
        if (myBattleBean != null) {
            battleStaList.remove(myBattleBean);
            mAgainstProfit.setTotalProfit(battleStaList);
        }
    }

    private void updateLastOperateData(BattleKlineOperate data, String type) {
        if (data.getBattleStatus() == BattleKline.STATUS_END) {
            battleFinish();
            return;
        }
        if (type.equalsIgnoreCase(BattleKline.BUY)) {
            mOperateView.buySuccess();
        } else if (type.equalsIgnoreCase(BattleKline.SELL)) {
            mOperateView.clearSuccess();
        }
        if (data.getStatus() == BattleKline.STATUS_END) {
            mOperateView.complete();
        } else {
            if (data.getNext() != null) {
                mKlineView.addKlineData(data.getNext());
            }
        }
        mOperateView.setTotalProfit(data.getProfit());
        mOperateView.setPositionProfit(data.getPositions());
    }

    @Override
    protected void clearOperate() {
        Client.requestKlineBattleSell().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateLastOperateData(data, BattleKline.SELL);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    @Override
    protected void passOperate() {
        Client.requestKlineBattlePass().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateLastOperateData(data, BattleKline.PASS);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
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
