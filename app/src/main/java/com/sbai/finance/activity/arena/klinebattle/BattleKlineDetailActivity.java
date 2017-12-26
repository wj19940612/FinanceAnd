package com.sbai.finance.activity.arena.klinebattle;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.kgame.GamePusher;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.BattleKlineInfo;
import com.sbai.finance.model.klinebattle.BattleKlineOperate;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Network;

import java.util.Collections;
import java.util.List;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * k线对决 多人页面
 */
public class BattleKlineDetailActivity extends SingleKlineExerciseActivity {

    private List<BattleKlineInfo> mBattleKlineInfos;
    private boolean mNoneOperate = true;
    private OnPushReceiveListener<BattleKlineInfo> mKlineBattlePushReceiverListener = new OnPushReceiveListener<BattleKlineInfo>() {
        @Override
        public void onPushReceive(BattleKlineInfo battleKlineInfo, String originalData) {
            if (battleKlineInfo != null) {
                onBattleKlinePushReceived(battleKlineInfo);
            }
        }
    };

    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                GamePusher.get().open();
                if (mBattleKline == null) {
                    requestBattleInfo();
                }
            }
        }
    };

    private void onBattleKlinePushReceived(final BattleKlineInfo battleKlineInfo) {
        if (battleKlineInfo.getCode() == BattleKline.PUSH_CODE_AGAINST_PROFIT) {
            if (battleKlineInfo.getUserId() != LocalUser.getUser().getUserInfo().getId()) {
                updateLastProfitData(battleKlineInfo);
            }
        } else if (battleKlineInfo.getCode() == BattleKline.PUSH_CODE_BATTLE_FINISH) {
            battleFinish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
        GamePusher.get().setOnPushReceiveListener(mKlineBattlePushReceiverListener);
        requestBattleInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScheduleJob();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    private void requestBattleInfo() {
        Client.requestKlineBattleInfo().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKline>, BattleKline>() {
                    @Override
                    protected void onRespSuccessData(BattleKline data) {
                        updateBattleData(data);
                    }
                }).fireFree();
    }

    private void requestCurrentBattle() {
        Client.getCurrentKlineBattle().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKlineInfo>, BattleKlineInfo>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineInfo data) {
                        if (data.getStatus() == BattleKline.STATUS_END) {
                            battleFinish();
                        }
                    }
                }).fireFree();
    }

    protected void updateBattleData(BattleKline data) {
        mBattleKline = data;
        updateCountDownTime();
        if (mBattleKline.getBattleStaList() == null) return;
        if (mBattleKline.getBattleStaList().size() > 0) {
            if (mBattleKline.getBattleStaList().get(0).getBattleStatus() == BattleKline.STATUS_END) {
                battleFinish();
                return;
            }
        }
        mKlineView.initKlineDataList(mBattleKline.getUserMarkList());
        mBattleKlineInfos = mBattleKline.getBattleStaList();
        updateLastProfitData();
        if (mBattleKline.getUserMarkList() != null) {
            int size = mBattleKline.getUserMarkList().size();
            if (size > 0) {
                BattleKlineData battleKlineData = mBattleKline.getUserMarkList().get(size - 2);
                if (battleKlineData.getMark().equalsIgnoreCase(BattleKlineData.MARK_HOLD_PASS) ||
                        battleKlineData.getMark().equalsIgnoreCase(BattleKlineData.MARK_BUY)) {
                    mOperateView.buySuccess();
                    mHasPosition = true;
                }
            }
            mRemainKlineAmount = data.getLine();
            updateRemainKlineAmount();
        }
    }

    private void updateLastProfitData(BattleKlineInfo battleKlineInfo) {
        if (mBattleKlineInfos == null) return;
        for (BattleKlineInfo item : mBattleKlineInfos) {
            if (item.getUserId() == battleKlineInfo.getUserId()) {
                item.setProfit(battleKlineInfo.getProfit());
                item.setPositions(battleKlineInfo.getPositions());
                item.setStatus(battleKlineInfo.getStatus());
            }
        }
        updateLastProfitData();
    }

    private void updateLastProfitData() {
        if (mBattleKlineInfos == null) return;
        Collections.sort(mBattleKlineInfos, Collections.reverseOrder());
        setRankValueByProfit(mBattleKlineInfos);
        for (BattleKlineInfo battleBean : mBattleKlineInfos) {
            if (battleBean != null) {
                if (battleBean.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                    if (battleBean.getBattleStatus() == BattleKline.STATUS_END) {
                        battleFinish();
                        return;
                    }
                    mOperateView.setRank(battleBean.getSort());
                    if (mNoneOperate && battleBean.getProfit() == 0 && !mHasPosition) {
                        mOperateView.clearTotalProfit();
                    } else {
                        mOperateView.setTotalProfit(battleBean.getProfit());
                    }
                    if (mHasPosition) {
                        mOperateView.setPositionProfit(battleBean.getPositions());
                    } else {
                        mOperateView.clearPositionProfit();
                    }
                    if (battleBean.getStatus() == BattleKline.STATUS_END) {
                        mOperateView.complete();
                    }
                }
            }
        }
        mAgainstProfit.setTotalProfit(mBattleKlineInfos);
    }

    protected void updateMyLastOperateData(BattleKlineOperate data, String type) {
        if (data.getBattleStatus() == BattleKline.STATUS_END) {
            battleFinish();
            return;
        }
        updateOperateView(type);
        updateLastProfitData(data);
        updateNextKlineView(data.getNext());
    }

    @Override
    protected void onCountDownFinish() {
        startScheduleJob(5 * 1000);
        mOperateView.showWaitFinishView();
    }

    @Override
    public void onTimeUp(int count) {
        requestCurrentBattle();
    }


    @Override
    protected void buyOperate() {
        mNoneOperate = false;
        mOperateView.disableOperateView();
        Client.requestKlineBattleBuy().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateMyLastOperateData(data, BattleKline.BUY);
                    }

                    @Override
                    public void onFinish() {
                        mOperateView.enableOperateView();
                    }
                }).fireFree();
    }

    @Override
    protected void clearOperate() {
        mNoneOperate = false;
        mOperateView.disableOperateView();
        Client.requestKlineBattleSell().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateMyLastOperateData(data, BattleKline.SELL);
                    }

                    @Override
                    public void onFinish() {
                        mOperateView.enableOperateView();
                    }
                }).fireFree();
    }

    @Override
    protected void passOperate() {
        if (mHasPosition) {
            mNoneOperate = false;
        }
        mOperateView.disableOperateView();
        Client.requestKlineBattlePass().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKlineOperate>, BattleKlineOperate>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineOperate data) {
                        updateMyLastOperateData(data, BattleKline.PASS);
                    }

                    @Override
                    public void onFinish() {
                        mOperateView.enableOperateView();
                    }
                }).fireFree();
    }

    private void setRankValueByProfit(List<BattleKlineInfo> battleKlineInfos) {
        int rank = 1;//名次
        int size = battleKlineInfos.size();
        for (int i = 0; i < size - 1; i++) {
            int n = checkContinue(battleKlineInfos, battleKlineInfos.get(i).getProfit());
            if (n == 1) {
                battleKlineInfos.get(i).setSort(rank++);
            } else {
                //收益相同，名次相同
                for (int j = 0; j < n; j++) {
                    battleKlineInfos.get(i + j).setSort(rank);
                    ;
                }
                rank++;
                i = i + n - 1;//连续n个相同的收益，排名一样
            }
        }
        battleKlineInfos.get(size - 1).setSort(rank);
    }

    private int checkContinue(List<BattleKlineInfo> battleBeans, double profit) {
        int count = 0;//统计多少个连续相同的profit
        for (int i = 0; i < battleBeans.size(); i++) {
            if (battleBeans.get(i).getProfit() == profit) {
                count++;
            }
        }
        return count;
    }

}
