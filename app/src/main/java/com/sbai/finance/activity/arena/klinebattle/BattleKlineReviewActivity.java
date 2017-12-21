package com.sbai.finance.activity.arena.klinebattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
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
    AutofitTextView mUserNameView;
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
    private int mUserId;
    private String mUserName;
    private String mUserPortrait;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_kline_review);
        ButterKnife.bind(this);
        translucentStatusBar();
        initData(getIntent());
        initKlineView();
        requestReviewData();
    }

    private void requestReviewData() {
        if (mType.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
            mImgRank.setVisibility(View.GONE);
            updateKlineData();
        } else {
            Client.requestKlineBattleReview(mUserId).setTag(TAG)
                    .setCallback(new Callback2D<Resp<BattleKline>, BattleKline>() {
                        @Override
                        protected void onRespSuccessData(BattleKline data) {
                            mBattleKline = data;
                            updateKlineData();
                        }
                    }).fireFree();
        }
    }

    private void updateKlineData() {
        if (mBattleKline != null) {
            int size = mBattleKline.getUserMarkList().size();
            mKlineView.initKlineDataList(mBattleKline.getUserMarkList().subList(0, size - 1));
            mKlineView.setLastInvisibleData(mBattleKline.getUserMarkList().get(size - 1));
        }
        if (mType.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
            setTotalProfit(mProfit);
        } else {
            setTotalProfit(mBattleKline.getStaInfo().getProfit());
        }
        setSelfUserInfo();
        updateBottomView();
    }


    public void setTotalProfit(double totalProfit) {
        if (totalProfit == 0) {
            mTotalProfit.setText("0.00%");
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
        mUserId = getIntent().getIntExtra(ExtraKeys.USER_ID, LocalUser.getUser().getUserInfo().getId());
        mUserName = getIntent().getStringExtra(ExtraKeys.USER_NAME);
        mUserPortrait = getIntent().getStringExtra(ExtraKeys.User_Portrait);
        if (TextUtils.isEmpty(mUserName)) {
            mUserName = LocalUser.getUser().getUserInfo().getUserName();
        }
        if (TextUtils.isEmpty(mUserPortrait)) {
            mUserPortrait = LocalUser.getUser().getUserInfo().getUserPortrait();
        }
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
                .load(mUserPortrait)
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mImgAvatar);
        mUserNameView.setText(mUserName);
    }

    private void updateBottomView() {
        if (mBattleKline == null) return;
        mBottomView.updateStock(mBattleKline.getBattleStockName(), mBattleKline.getBattleStockCode(),
                mBattleKline.getBattleStockStartTime(), mBattleKline.getBattleStockEndTime(), mBattleKline.getRise());
    }
}
