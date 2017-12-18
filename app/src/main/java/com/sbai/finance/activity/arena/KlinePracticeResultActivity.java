package com.sbai.finance.activity.arena;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.arena.klinebattle.BattleKlineReviewActivity;
import com.sbai.finance.activity.arena.klinebattle.SingleKlineExerciseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.KlineOtherName;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.KlineBottomResultView;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class KlinePracticeResultActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.upDown)
    TextView mUpDown;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.otherName)
    TextView mOtherName;
    @BindView(R.id.resultTip)
    TextView mResultTip;
    @BindView(R.id.nameLayout)
    LinearLayout mNameLayout;
    @BindView(R.id.reLookBtn)
    TextView mReLookBtn;
    @BindView(R.id.moreOneBtn)
    TextView mMoreOneBtn;
    @BindView(R.id.bottomView)
    KlineBottomResultView mBottomView;

    private double mProfit;
    private BattleKline mBattleKlineData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_practice_result);
        ButterKnife.bind(this);
        initView();
        initData();
        requestOtherName();
    }

    private void initData() {
        mProfit = getIntent().getDoubleExtra(ExtraKeys.BATTLE_PROFIT, 0);
        mBattleKlineData = getIntent().getParcelableExtra(ExtraKeys.BATTLE_KLINE_DATA);
    }

    private void initView() {

    }

    private void requestOtherName() {
        Client.requestKlineOtherName(mProfit).setTag(TAG).setCallback(new Callback2D<Resp<KlineOtherName>, KlineOtherName>() {
            @Override
            protected void onRespSuccessData(KlineOtherName data) {
                updateData(data);
            }
        }).fireFree();
    }


    private void updateData(KlineOtherName data) {
        updateBottomView();

        if (LocalUser.getUser().getUserInfo() != null) {
            GlideApp.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .circleCrop().into(mAvatar);

            if (mProfit >= 0) {
                mUpDown.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
                mUpDown.setText("+" + String.format("%.2f", mProfit * 100) + "%");
            } else {
                mUpDown.setTextColor(ContextCompat.getColor(this, R.color.greenAssist));
                mUpDown.setText(String.format("%.2f", mProfit * 100) + "%");
            }


            mOtherName.setText(data.getAppellation());
            mResultTip.setText(data.getDoc());
        }
    }

    private void updateBottomView() {
        if (mBattleKlineData == null) return;
        mBottomView.updateStock(mBattleKlineData.getBattleVarietyName(), mBattleKlineData.getBattleVarietyCode(),
                mBattleKlineData.getBattleStockStartTime(), mBattleKlineData.getBattleStockEndTime(), mBattleKlineData.getRise());
    }


    @OnClick({R.id.moreOneBtn, R.id.reLookBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.moreOneBtn:
                Launcher.with(this, SingleKlineExerciseActivity.class).putExtra(ExtraKeys.GUESS_TYPE, BattleKline.TYPE_EXERCISE).execute();
                break;
            case R.id.reLookBtn:
                Launcher.with(this, BattleKlineReviewActivity.class)
                        .putExtra(ExtraKeys.GUESS_TYPE, BattleKline.TYPE_EXERCISE)
                        .putExtra(ExtraKeys.BATTLE_PROFIT, mProfit)
                        .putExtra(ExtraKeys.BATTLE_KLINE_DATA, mBattleKlineData)
                        .execute();
                break;

        }
    }
}