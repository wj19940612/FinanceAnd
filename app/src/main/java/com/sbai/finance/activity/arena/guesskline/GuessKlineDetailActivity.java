package com.sbai.finance.activity.arena.guesskline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.klinebattle.GuessKline;
import com.sbai.finance.view.training.guesskline.AgainstProfitView;
import com.sbai.finance.view.training.guesskline.CountDownView;
import com.sbai.finance.view.training.guesskline.GuessKlineOperateView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * k线对决页面
 */

public class GuessKlineDetailActivity extends BaseActivity {
    @BindView(R.id.back)
    TextView mBack;
    @BindView(R.id.pkType)
    TextView mPkType;
    @BindView(R.id.countdown)
    CountDownView mCountdown;
    @BindView(R.id.againstProfit)
    AgainstProfitView mAgainstProfit;
    @BindView(R.id.operate)
    GuessKlineOperateView mOperate;
    private String mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_kline_detail);
        ButterKnife.bind(this);
        initData(getIntent());
        initTitleView();
    }

    private void initData(Intent intent) {
        mType = intent.getStringExtra(ExtraKeys.GUESS_TYPE);
    }

    private void initTitleView() {
        if (TextUtils.isEmpty(mType)) {
            mType = GuessKline.TYPE_EXERCISE;
        } else if (mType.equalsIgnoreCase(GuessKline.TYPE_1V1)) {
            mPkType.setText(R.string.one_pk);
        } else if (mType.equalsIgnoreCase(GuessKline.TYPE_4V4)) {
            mPkType.setText(R.string.four_pk);
        }
        mAgainstProfit.setPkType(mType);
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        getActivity().onBackPressed();
    }
}
