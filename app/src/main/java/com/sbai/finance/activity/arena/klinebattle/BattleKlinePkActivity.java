package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.activity.arena.KLineResultActivity;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.BattleKlineOperate;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * k线Pk练习
 */

public class BattleKlinePkActivity extends BattleKlineDetailActivity {

    private boolean mHasPosition;
    private List<BattleKline.BattleBean> mBattleStaList;

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
        int totalTime = (int) ((mBattleKline.getEndTime() - SysTime.getSysTime().getSystemTimestamp()));
        mCountdown.setTotalTime(totalTime, new KlineBattleCountDownView.OnCountDownListener() {
            @Override
            public void finish() {
                //  battleFinish();
            }
        });
        if (mBattleKline.getBattleStaList().size() > 0) {
            if (mBattleKline.getBattleStaList().get(0).getBattleStatus() == BattleKline.STATUS_END) {
                battleFinish();
            }
        }
        mKlineView.initKlineDataList(mBattleKline.getUserMarkList());
        mBattleStaList = mBattleKline.getBattleStaList();
        updateLastProfitData();
        if (mBattleKline.getUserMarkList() != null) {
            int size = mBattleKline.getUserMarkList().size();
            if (size > 1) {
                BattleKlineData battleKlineData = mBattleKline.getUserMarkList().get(size - 2);
                if (battleKlineData.getMark().equalsIgnoreCase(BattleKlineData.MARK_HOLD_PASS)) {
                    mOperateView.buySuccess();
                    mHasPosition = true;
                }
            }
            mRemainKlineAmount = data.getLine();
            setRemainKline();
        }
    }

    @Override
    protected void onBattleKlinePushReceived(BattleKline.BattleBean battleBean) {
        super.onBattleKlinePushReceived(battleBean);
        if (battleBean.getCode() == String.valueOf(BattleKline.PUSH_CODE_AGAINST_PROFIT)) {
            updateLastProfitData(battleBean);
        } else if (battleBean.getCode() == String.valueOf(BattleKline.PUSH_CODE_BATTLE_FINISH)) {
            battleFinish();
        }
    }

    private void updateLastProfitData(BattleKline.BattleBean battleBean) {
        if (mBattleStaList == null) return;
        for (BattleKline.BattleBean item : mBattleStaList) {
            if (item.getUserId() == battleBean.getUserId()) {
                item.setProfit(battleBean.getProfit());
            }
        }
    }

    private void updateLastProfitData() {
        if (mBattleStaList == null) return;
        Collections.sort(mBattleStaList, Collections.<BattleKline.BattleBean>reverseOrder());
        setRankValueByProfit(mBattleStaList);
        for (BattleKline.BattleBean battleBean : mBattleStaList) {
            if (battleBean != null) {
                if (battleBean.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                    if (battleBean.getBattleStatus() == BattleKline.STATUS_END) {
                        battleFinish();
                        return;
                    }
                    if (battleBean.getStatus() == BattleKline.STATUS_END) {
                        mOperateView.complete();
                    }
                    mOperateView.setRank(battleBean.getSort());
                    mOperateView.setTotalProfit(battleBean.getProfit());
                    mOperateView.setPositionProfit(battleBean.getPositions());
                }
            }
        }
        mAgainstProfit.setTotalProfit(mBattleStaList);
    }

    private void updateMyLastOperateData(BattleKlineOperate data, String type) {
        if (data.getBattleStatus() == BattleKline.STATUS_END) {
            battleFinish();
            return;
        }
        if (type.equalsIgnoreCase(BattleKline.BUY)) {
            mHasPosition = true;
            mKlineView.getLastData().setMark(BattleKlineData.MARK_BUY);
            mOperateView.buySuccess();
        } else if (type.equalsIgnoreCase(BattleKline.SELL)) {
            mHasPosition = false;
            mKlineView.getLastData().setMark(BattleKlineData.MARK_SELL);
            mOperateView.clearSuccess();
        } else {
            if (mHasPosition) {
                mKlineView.getLastData().setMark(BattleKlineData.MARK_HOLD_PASS);
            } else {
                mKlineView.getLastData().setMark(BattleKlineData.MARK_PASS);
            }
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
    protected void buyOperate() {
        Client.requestKlineBattleBuy().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateMyLastOperateData(data, BattleKline.BUY);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    @Override
    protected void clearOperate() {
        Client.requestKlineBattleSell().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateMyLastOperateData(data, BattleKline.SELL);
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
                        updateMyLastOperateData(data, BattleKline.PASS);
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
        Launcher.with(getActivity(), KLineResultActivity.class)
                .putExtra(ExtraKeys.GUESS_TYPE, mType)
                .execute();
        finish();
    }

    private void setRankValueByProfit(List<BattleKline.BattleBean> battleBeans) {
        int rank = 1;//名次
        int size = battleBeans.size();
        for (int i = 0; i < size - 1; i++) {
            int n = checkContinue(battleBeans, battleBeans.get(i).getProfit());
            if (n == 1) {
                battleBeans.get(i).setSort(rank++);
            } else {
                //收益相同，名次相同
                for (int j = 0; j < n; j++) {
                    battleBeans.get(i + j).setSort(rank);
                    ;
                }
                rank++;
                i = i + n - 1;//连续n个相同的收益，排名一样
            }
        }
        battleBeans.get(size - 1).setSort(rank);
    }

    private int checkContinue(List<BattleKline.BattleBean> battleBeans, double profit) {
        int count = 0;//统计多少个连续相同的profit
        for (int i = 0; i < battleBeans.size(); i++) {
            if (battleBeans.get(i).getProfit() == profit) {
                count++;
            }
        }
        return count;
    }
}
