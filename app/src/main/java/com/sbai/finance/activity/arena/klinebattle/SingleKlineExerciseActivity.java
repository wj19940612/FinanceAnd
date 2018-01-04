package com.sbai.finance.activity.arena.klinebattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sbai.chart.ColorCfg;
import com.sbai.chart.KlineChart;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.arena.KLineResultActivity;
import com.sbai.finance.activity.arena.KlinePracticeResultActivity;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.klinebattle.BattleKlineChart;
import com.sbai.finance.view.training.guesskline.AgainstProfitView;
import com.sbai.finance.view.training.guesskline.BattleKlineOperateView;
import com.sbai.finance.view.training.guesskline.KlineBattleCountDownView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * k线对决 单人练习
 */
public class SingleKlineExerciseActivity extends BaseActivity {

    @BindView(R.id.againstProfit)
    AgainstProfitView mAgainstProfit;
    @BindView(R.id.operateView)
    BattleKlineOperateView mOperateView;
    @BindView(R.id.klineView)
    BattleKlineChart mKlineView;
    @BindView(R.id.title)
    TitleBar mTitle;

    KlineBattleCountDownView mCountdown;

    protected String mType;
    protected int mCurrentIndex;
    protected int mRemainKlineAmount;
    protected BattleKline mBattleKline;
    protected boolean mHasPosition;

    protected List<BattleKlineData> mBattleUserMarkList;
    private int mPositionIndex = -1;

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
        requestKlineData();
    }

    private void initData(Intent intent) {
        mType = intent.getStringExtra(ExtraKeys.GUESS_TYPE);
    }

    protected void initTitleView() {
        initTitleView(getString(R.string.single_exercise));
    }

    protected void initTitleView(String titleText) {
        View customView = mTitle.getCustomView();
        TextView pkType = customView.findViewById(R.id.pkType);
        mCountdown = customView.findViewById(R.id.countdown);
        pkType.setText(titleText);
        mAgainstProfit.setPkType(mType);
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

    protected void requestKlineData() {
        Client.getSingleKlineBattleData().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKline>, BattleKline>() {
                    @Override
                    protected void onRespSuccessData(BattleKline data) {
                        updateBattleData(data);
                    }
                }).fireFree();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountdown.cancelCount();
    }

    protected void updateBattleData(BattleKline data) {
        if (data == null) return;
        mBattleKline = data;
        mBattleUserMarkList = mBattleKline.getUserMarkList();
        if (mBattleUserMarkList != null && mType.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
            mBattleUserMarkList.get(mBattleUserMarkList.size() - 1).setMark(BattleKlineData.MARK_NOT_OP);
            List<BattleKlineData> subList = new ArrayList<>();
            for (int i = 0; i < mBattleUserMarkList.size(); i++) {
                subList.add(mBattleUserMarkList.get(i));
                if (mBattleUserMarkList.get(i).getMark().equalsIgnoreCase(BattleKlineData.MARK_OP)) {
                    mCurrentIndex = i;
                    break;
                }
            }
            mKlineView.initKlineDataList(subList);
        }
        mRemainKlineAmount = mBattleKline.getLine();
        updateCountDownTime();
        updateRemainKlineAmount();
    }

    protected void buyOperate() {
        mPositionIndex = mCurrentIndex;
        updateLastOperateData(BattleKline.BUY);
    }

    protected void clearOperate() {
        updateLastOperateData(BattleKline.SELL);
    }

    protected void passOperate() {
        updateLastOperateData(BattleKline.PASS);
    }

    private void updateLastOperateData(String type) {
        if (mBattleUserMarkList == null) return;
        if (mCurrentIndex < mBattleUserMarkList.size()) {
            if (!isBattleFinish()) {
                updateOperateView(type);
                updateLastProfit(type);
                updateNextKlineView(mBattleUserMarkList.get(++mCurrentIndex));
            }
        }
    }

    protected boolean isBattleFinish() {
        if (mCurrentIndex == mBattleUserMarkList.size() - 2) {
            updateLastProfit(BattleKline.PASS);
            mBattleUserMarkList.get(mCurrentIndex + 1).setMark(BattleKlineData.MARK_SELL);
            updateNextKlineView(mBattleUserMarkList.get(mCurrentIndex + 1));
            battleFinish();
            return true;
        }
        return false;
    }

    protected void updateOperateView(String type) {
        if (type.equalsIgnoreCase(BattleKline.BUY)) {
            mHasPosition = true;
            if (mKlineView.getLastData() != null) {
                mKlineView.getLastData().setMark(BattleKlineData.MARK_BUY);
            }
            mOperateView.buySuccess();
        } else if (type.equalsIgnoreCase(BattleKline.SELL)) {
            mHasPosition = false;
            if (mKlineView.getLastData() != null) {
                mKlineView.getLastData().setMark(BattleKlineData.MARK_SELL);
            }
            mOperateView.clearSuccess();
        } else {
            if (mKlineView.getLastData() != null) {
                if (mHasPosition) {
                    mKlineView.getLastData().setMark(BattleKlineData.MARK_HOLD_PASS);
                } else {
                    mKlineView.getLastData().setMark(BattleKlineData.MARK_PASS);
                }
            }
        }
    }

    protected void updateNextKlineView(BattleKlineData battleKlineData) {
        updateRemainKlineAmount();
        mKlineView.addKlineData(battleKlineData);
    }

    protected void updateRemainKlineAmount() {
        mOperateView.setRemainKline(mRemainKlineAmount--);
    }

    protected void updateCountDownTime() {
        long totalTime = ((mBattleKline.getEndTime() - SysTime.getSysTime().getSystemTimestamp()));
        mCountdown.setTotalTime(totalTime, new KlineBattleCountDownView.OnCountDownListener() {
            @Override
            public void finish() {
                onCountDownFinish();
            }
        });
    }

    protected void onCountDownFinish() {
        if (mHasPosition) {
            int size = mBattleUserMarkList.size();
            for (int i = mCurrentIndex; i < size - 1; i++) {
                mBattleUserMarkList.get(i).setMark(BattleKlineData.MARK_HOLD_PASS);
                mKlineView.addKlineData(mBattleUserMarkList.get(i));
            }
            mBattleUserMarkList.get(size - 1).setMark(BattleKlineData.MARK_SELL);
            mKlineView.addKlineData(mBattleUserMarkList.get(size - 1));

            mCurrentIndex = size - 2;
            updateLastProfit(BattleKline.PASS);
        }
        battleFinish();
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

    private void updateLastProfit(String type) {
        BattleKlineData positionKlineData = null;
        BattleKlineData nextKlineData = mBattleUserMarkList.get(mCurrentIndex + 1);
        if (type.equalsIgnoreCase(BattleKline.PASS)) {
            if (mHasPosition) {
                positionKlineData = mBattleUserMarkList.get(mPositionIndex);
            }
        } else {
            positionKlineData = mBattleUserMarkList.get(mCurrentIndex);
        }
        if (positionKlineData != null) {
            double positionProfit = (nextKlineData.getClosePrice() - positionKlineData.getClosePrice()) / positionKlineData.getClosePrice();
            if (type.equalsIgnoreCase(BattleKline.BUY) || mHasPosition) {
                mOperateView.setTotalProfit(positionProfit - mOperateView.getLastPosition() + mOperateView.getTotalProfit());
            }
            if (mHasPosition) {
                nextKlineData.setPositions(positionProfit);
                mOperateView.setPositionProfit(positionProfit);
            }
        }
    }
}
