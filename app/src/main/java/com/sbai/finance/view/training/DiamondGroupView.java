package com.sbai.finance.view.training;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
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

    @OnClick({R.id.klineImg, R.id.describe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.klineImg:
                break;
            case R.id.describe:
                break;
        }
    }

    public void startErrorAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "rotation", 0f, -20f, 0f);
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(1000);
        animator.start();
    }

    public void startDisappearAnim() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(rotation).with(scaleX).with(scaleY).with(alpha);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(INVISIBLE);
            }
        });
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.setDuration(2000);
        animSet.start();
    }

    public void startAppearAnim() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0f, 1.2f, 1f,1.2f,1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0f, 1.2f, 1f,1.2f,1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.setDuration(1000);
        animSet.start();
    }

    public void setSelected(boolean selected) {
        mKlineView.setSelected(selected);
    }

}