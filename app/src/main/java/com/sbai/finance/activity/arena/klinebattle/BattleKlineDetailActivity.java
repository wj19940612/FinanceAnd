package com.sbai.finance.activity.arena.klinebattle;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.kgame.GamePusher;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.ToastUtil;

import java.util.Collections;
import java.util.List;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * k线对决 多人页面
 */
public class BattleKlineDetailActivity extends SingleKlineExerciseActivity {

    private List<BattleKline.BattleBean> mBattleStaList;
    private OnPushReceiveListener<BattleKline.BattleBean> mKlineBattlePushReceiverListener = new OnPushReceiveListener<BattleKline.BattleBean>() {
        @Override
        public void onPushReceive(BattleKline.BattleBean battleBean, String originalData) {
            if (battleBean != null) {
                onBattleKlinePushReceived(battleBean);
            }
        }
    };

    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                GamePusher.get().open();
            }
        }
    };

    private void onBattleKlinePushReceived(final BattleKline.BattleBean battleBean) {
        if (battleBean.getCode() == BattleKline.PUSH_CODE_AGAINST_PROFIT) {
            updateLastProfitData(battleBean);
        } else if (battleBean.getCode() == BattleKline.PUSH_CODE_BATTLE_FINISH) {
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
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
        GamePusher.get().removeOnPushReceiveListener();
    }

    private void requestBattleInfo() {
        Client.requestKlineBattleInfo().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKline>, BattleKline>() {
                    @Override
                    protected void onRespSuccessData(BattleKline data) {
                        if (data != null) {
                            updateBattleData(data);
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        if (failedResp.getCode() == BattleKline.PUSH_CODE_BATTLE_FINISH) {
                            battleFinish();
                        } else {
                            ToastUtil.show(failedResp.getMsg());
                        }
                    }
                }).fireFree();
    }

    protected void updateBattleData(BattleKline data) {
        mBattleKline = data;
        updateCountDownTime();
        if (mBattleKline.getBattleStaList().size() > 0) {
            if (mBattleKline.getBattleStaList().get(0).getBattleStatus() == BattleKline.STATUS_END) {
                battleFinish();
                return;
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
            updateRemainKlineAmount();
        }
    }

    private void updateLastProfitData(BattleKline.BattleBean battleBean) {
        if (mBattleStaList == null) return;
        for (BattleKline.BattleBean item : mBattleStaList) {
            if (item.getUserId() == battleBean.getUserId()) {
                item.setProfit(battleBean.getProfit());
                item.setPositions(battleBean.getPositions());
            }
        }
        updateLastProfitData();
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
                    if (mHasPosition) {
                        mOperateView.setPositionProfit(battleBean.getPositions());
                    } else {
                        mOperateView.clearPositionProfit();
                    }
                }
            }
        }
        mAgainstProfit.setTotalProfit(mBattleStaList);
    }

    protected void updateMyLastOperateData(BattleKline.BattleBean data, String type) {
        if (data.getBattleStatus() == BattleKline.STATUS_END) {
            battleFinish();
            return;
        }
        if (data.getStatus() == BattleKline.STATUS_END) {
            mOperateView.complete();
        }
        updateOperateView(type);
        updateLastProfitData(data);
        updateNextKlineView(data.getNext());
    }

    @Override
    protected void buyOperate() {
        Client.requestKlineBattleBuy().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<BattleKline.BattleBean>, BattleKline.BattleBean>() {
                    @Override
                    protected void onRespSuccessData(BattleKline.BattleBean data) {
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
                .setCallback(new Callback2D<Resp<BattleKline.BattleBean>, BattleKline.BattleBean>() {
                    @Override
                    protected void onRespSuccessData(BattleKline.BattleBean data) {
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
                .setCallback(new Callback2D<Resp<BattleKline.BattleBean>, BattleKline.BattleBean>() {
                    @Override
                    protected void onRespSuccessData(BattleKline.BattleBean data) {
                        updateMyLastOperateData(data, BattleKline.PASS);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
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
