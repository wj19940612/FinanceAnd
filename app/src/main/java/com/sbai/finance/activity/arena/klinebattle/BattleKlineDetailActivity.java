package com.sbai.finance.activity.arena.klinebattle;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbai.chart.ColorCfg;
import com.sbai.chart.KlineChart;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.kgame.GamePusher;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.klinebattle.BattleKlineChart;
import com.sbai.finance.view.training.guesskline.AgainstProfitView;
import com.sbai.finance.view.training.guesskline.BattleKlineOperateView;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;

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
    //start from 0
    protected int mCurrentIndex = 39;
    protected int mRemainKlineAmount;
    protected BattleKline mBattleKline;
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

    protected void onBattleKlinePushReceived(final BattleKline.BattleBean battleBean) {
        if (!LocalUser.getUser().isLogin()) return;
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
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        GamePusher.get().setOnPushReceiveListener(mKlineBattlePushReceiverListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GamePusher.get().removeOnPushReceiveListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountdown.cancelCount();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
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


    @OnClick(R.id.back)
    public void onViewClicked() {
        getActivity().onBackPressed();
    }

    protected void setRemainKline() {
        mOperateView.setRemainKline(mRemainKlineAmount);
    }

    protected void buyOperate() {
    }

    protected void clearOperate() {
    }

    protected void passOperate() {
    }

    protected void battleFinish() {
        mCountdown.cancelCount();
    }

}
