package com.sbai.finance.activity.arena.klinebattle;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sbai.chart.ColorCfg;
import com.sbai.chart.KlineChart;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.arena.KLineResultActivity;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.kgame.GamePusher;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.klinebattle.BattleKlineChart;
import com.sbai.finance.view.training.guesskline.AgainstProfitView;
import com.sbai.finance.view.training.guesskline.BattleKlineOperateView;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * k线对决页面
 */

public class BattleKlineDetailActivity extends BaseActivity {
    @BindView(R.id.againstProfit)
    AgainstProfitView mAgainstProfit;
    @BindView(R.id.operateView)
    BattleKlineOperateView mOperateView;
    @BindView(R.id.klineView)
    BattleKlineChart mKlineView;
    @BindView(R.id.title)
    TitleBar mTitle;
    TextView mPkType;
    KlineBattleCountDownView mCountdown;
    protected String mType;
    protected int mCurrentIndex = 39;
    protected int mRemainKlineAmount;
    protected BattleKline mBattleKline;
    protected boolean mHasPosition;
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
                GamePusher.get().connect();
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
        setContentView(R.layout.activity_battle_kline_detail);
        ButterKnife.bind(this);
        translucentStatusBar();
        initData(getIntent());
        initTitleView();
        initKlineView();
        initOperateView();
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
        GamePusher.get().setOnPushReceiveListener(mKlineBattlePushReceiverListener);
        if (!TextUtils.isEmpty(mType) && !mType.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
            requestBattleInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountdown.cancelCount();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
        GamePusher.get().removeOnPushReceiveListener();
    }

    private void initKlineView() {
        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(7);
        settings2.setNumberScale(2);
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        settings2.setColorCfg(new ColorCfg()
                .put(ColorCfg.BASE_LINE, "#2a2a2a"));
        settings2.setIndexesEnable(true);
        settings2.setIndexesBaseLines(2);
        mKlineView.setDayLine(true);
        mKlineView.setSettings(settings2);
    }

    private void initOperateView() {
        mOperateView.setSelfAvatar();
        mOperateView.setOperateListener(new BattleKlineOperateView.OperateListener() {
            @Override
            public void buy() {
                buyOperate();
            }

            @Override
            public void clear() {
                clearOperate();
            }

            @Override
            public void pass() {
                passOperate();
            }
        });
    }

    private void initData(Intent intent) {
        mType = intent.getStringExtra(ExtraKeys.GUESS_TYPE);
    }

    private void initTitleView() {
        View customView = mTitle.getCustomView();
        mCountdown = customView.findViewById(R.id.countdown);
        mPkType = customView.findViewById(R.id.pkType);
        if (TextUtils.isEmpty(mType)) {
            mType = BattleKline.TYPE_EXERCISE;
            mPkType.setText(R.string.single_exercise);
        } else if (mType.equalsIgnoreCase(BattleKline.TYPE_1V1)) {
            mPkType.setText(R.string.one_vs_one_room);
        } else if (mType.equalsIgnoreCase(BattleKline.TYPE_4V4)) {
            mPkType.setText(R.string.four_pk_room);
        }
        mAgainstProfit.setPkType(mType);
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


    @OnClick(R.id.back)
    public void onViewClicked() {
        getActivity().onBackPressed();
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

    protected void updateRemainKlineAmount() {
        mOperateView.setRemainKline(mRemainKlineAmount--);
    }

    protected void updateCountDownTime() {
        long totalTime = ((mBattleKline.getEndTime() - SysTime.getSysTime().getSystemTimestamp()));
        mCountdown.setTotalTime(totalTime, new KlineBattleCountDownView.OnCountDownListener() {
            @Override
            public void finish() {
                if (mType.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
                    battleFinish();
                }
            }
        });
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

    protected void updateOperateView(String type) {
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
    }

    protected void updateNextKlineView(BattleKlineData battleKlineData) {
        mRemainKlineAmount = mRemainKlineAmount - 1;
        mOperateView.setRemainKline(mRemainKlineAmount);
        mKlineView.addKlineData(battleKlineData);
    }

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

    protected void battleFinish() {
        mCountdown.cancelCount();
        if (mType.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
            Launcher.with(getActivity(), KlinePracticeResultActivity.class)
                    .putExtra(ExtraKeys.BATTLE_KLINE_DATA, mBattleKline)
                    .putExtra(ExtraKeys.BATTLE_PROFIT, mOperateView.getTotalProfit())
                    .execute();
        } else {
            Launcher.with(getActivity(), KLineResultActivity.class)
                    .putExtra(ExtraKeys.GUESS_TYPE, mType)
                    .execute();
        }
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
