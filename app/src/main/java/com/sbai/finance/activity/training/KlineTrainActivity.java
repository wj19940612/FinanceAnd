package com.sbai.finance.activity.training;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.training.DiamondView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * K线概念训练页面
 */

public class KlineTrainActivity extends BaseActivity {
    @BindView(R.id.klineView)
    DiamondView mKlineView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_train);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.klineView)
    public void onViewClicked() {
        mKlineView.setSelected(true);
        mKlineView.startErrorAnim();
    }
}
