package com.sbai.finance.activity.arena.klinebattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.sbai.chart.ColorCfg;
import com.sbai.chart.KlineChart;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.klinebattle.KlineBattle;
import com.sbai.finance.view.klinebattle.BattleKline;
import com.sbai.finance.view.training.guesskline.AgainstProfitView;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;
import com.sbai.finance.view.training.guesskline.KlineBattleOperateView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * k线对决页面
 */

public class KlineBattleDetailActivity extends BaseActivity {
    @BindView(R.id.back)
    TextView mBack;
    @BindView(R.id.pkType)
    TextView mPkType;
    @BindView(R.id.countdown)
    KlineBattleCountDownView mCountdown;
    @BindView(R.id.againstProfit)
    AgainstProfitView mAgainstProfit;
    @BindView(R.id.operateView)
    KlineBattleOperateView mOperateView;
    @BindView(R.id.klineView)
    BattleKline mKlineView;
    private String mType;
    //start from 0
    protected int mCurrentIndex = 39;
    protected int mRemainKlineAmount;
    protected KlineBattle mKlineBattle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_battle_detail);
        ButterKnife.bind(this);
        initData(getIntent());
        initTitleView();
        initKlineView();
        initOperateView();
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
        mOperateView.setOperateListener(new KlineBattleOperateView.OperateListener() {
            @Override
            public void buy() {
                buy();
            }

            @Override
            public void clear() {
                clear();
            }

            @Override
            public void pass() {
                pass();
            }
        });
    }

    private void initData(Intent intent) {
        mType = intent.getStringExtra(ExtraKeys.GUESS_TYPE);
    }

    private void initTitleView() {
        if (TextUtils.isEmpty(mType)) {
            mType = KlineBattle.TYPE_EXERCISE;
            mPkType.setText(R.string.single_exercise);
        } else if (mType.equalsIgnoreCase(KlineBattle.TYPE_1V1)) {
            mPkType.setText(R.string.one_vs_one_room);
        } else if (mType.equalsIgnoreCase(KlineBattle.TYPE_4V4)) {
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

    protected void buy() {
    }

    protected void clear() {
    }

    protected void pass() {
    }

}
