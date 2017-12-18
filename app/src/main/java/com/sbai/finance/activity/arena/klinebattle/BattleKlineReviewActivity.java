package com.sbai.finance.activity.arena.klinebattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.chart.ColorCfg;
import com.sbai.chart.KlineChart;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.view.KlineBottomResultView;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.autofit.AutofitTextView;
import com.sbai.finance.view.klinebattle.BattleKlineChart;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 复盘界面
 */

public class BattleKlineReviewActivity extends BaseActivity {
    @BindView(R.id.title)
    TitleBar mTitle;
    @BindView(R.id.imgRank)
    ImageView mImgRank;
    @BindView(R.id.imgAvatar)
    ImageView mImgAvatar;
    @BindView(R.id.userName)
    AutofitTextView mUserName;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.profitArea)
    RelativeLayout mProfitArea;
    @BindView(R.id.klineView)
    BattleKlineChart mKlineView;
    @BindView(R.id.bottomView)
    KlineBottomResultView mBottomView;
    private String mType;
    private double mProfit;
    private BattleKline mBattleKline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_kline_review);
        ButterKnife.bind(this);
        initData(getIntent());
        initKlineView();
        updateViewData();
    }

    private void updateViewData() {
        if (mType.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
            if (mBattleKline != null) {
                mImgRank.setVisibility(View.GONE);
                mKlineView.initKlineDataList(mBattleKline.getUserMarkList().subList(0, mBattleKline.getUserMarkList().size() - 1));
            }
        }
        setTotalProfit(mProfit);
        setSelfUserInfo();
        updateBottomView();
    }

    public void setTotalProfit(double totalProfit) {
        totalProfit = Double.valueOf(FinanceUtil.formatWithScale(totalProfit));
        if (totalProfit == 0) {
            mTotalProfit.setText("0.00");
            mTotalProfit.setTextColor(ContextCompat.getColor(getActivity(), R.color.eighty_white));
        } else if (totalProfit > 0) {
            mTotalProfit.setText("+" + FinanceUtil.formatToPercentage(totalProfit));
            mTotalProfit.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
        } else {
            mTotalProfit.setText(FinanceUtil.formatToPercentage(totalProfit));
            mTotalProfit.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
        }
    }

    private void initData(Intent intent) {
        mType = intent.getStringExtra(ExtraKeys.GUESS_TYPE);
        mProfit = getIntent().getDoubleExtra(ExtraKeys.BATTLE_PROFIT, 0);
        mBattleKline = getIntent().getParcelableExtra(ExtraKeys.BATTLE_KLINE_DATA);
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


    public void setSelfUserInfo() {
        GlideApp.with(getActivity())
                .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mImgAvatar);
        mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
    }

    private void updateBottomView() {
        if (mBattleKline == null) return;
        mBottomView.updateStock(mBattleKline.getBattleVarietyName(), mBattleKline.getBattleVarietyCode(),
                mBattleKline.getBattleStockStartTime(), mBattleKline.getBattleStockEndTime(), mBattleKline.getRise());
    }
}
