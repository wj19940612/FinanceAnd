package com.sbai.finance.view.train;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * K线训练 菱形加K线知识组合布局页面
 */

public class DiamondGroupView extends RelativeLayout {
    @BindView(R.id.klineView)
    DiamondView mKlineView;
    @BindView(R.id.klineImg)
    ImageView mKlineImg;
    @BindView(R.id.describe)
    TextView mDescribe;

    public DiamondGroupView(Context context) {
        this(context, null);
    }

    public DiamondGroupView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DiamondGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_diamond_group, null, false);
        addView(view);
        ButterKnife.bind(this);
    }

    public void setBackground(int color) {
        mKlineView.setBackgroundColor(color);
    }

    public void setKlineImageVisible(boolean visible) {
        if (visible) {
            mKlineImg.setVisibility(VISIBLE);
        } else {
            mKlineImg.setVisibility(GONE);
        }
    }

    public void setKlineDescribeVisible(boolean visible) {
        if (visible) {
            mDescribe.setVisibility(VISIBLE);
        } else {
            mDescribe.setVisibility(GONE);
        }
    }

    public void startAnim() {
        mKlineView.startErrorAnim();
    }

    @OnClick({R.id.klineImg, R.id.describe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.klineImg:
                break;
            case R.id.describe:
                break;
        }
    }
}
