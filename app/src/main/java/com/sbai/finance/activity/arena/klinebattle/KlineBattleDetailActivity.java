package com.sbai.finance.activity.arena.klinebattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.klinebattle.KlineBattle;
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
    @BindView(R.id.operate)
    KlineBattleOperateView mOperate;
    private String mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_battle_detail);
        ButterKnife.bind(this);
        initData(getIntent());
        initTitleView();
    }

    private void initData(Intent intent) {
        mType = intent.getStringExtra(ExtraKeys.GUESS_TYPE);
    }

    private void initTitleView() {
        if (TextUtils.isEmpty(mType)) {
            mType = KlineBattle.TYPE_EXERCISE;
        } else if (mType.equalsIgnoreCase(KlineBattle.TYPE_1V1)) {
            mPkType.setText(R.string.one_vs_one_room);
        } else if (mType.equalsIgnoreCase(KlineBattle.TYPE_4V4)) {
            mPkType.setText(R.string.four_pk);
        }
        mAgainstProfit.setPkType(mType);
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        getActivity().onBackPressed();
    }
}
