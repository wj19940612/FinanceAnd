package com.sbai.finance.activity.training;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.utils.Display;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 怎么玩页面
 */

public class HowPlayActivity extends BaseActivity {

    @BindView(R.id.trainImg)
    ImageView mTrainImg;
    @BindView(R.id.content)
    TextView mContent;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.horizontalView)
    CardView mHorizontalView;
    @BindView(R.id.trainImg1)
    ImageView mTrainImg1;
    @BindView(R.id.content1)
    TextView mContent1;
    @BindView(R.id.confirm1)
    TextView mConfirm1;
    @BindView(R.id.verticalView)
    CardView mVerticalView;

    private Training mTraining;
    private boolean mIsHorizontal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_play);
        ButterKnife.bind(this);

        initData(getIntent());

        initView();

        requestTrainGuide();
    }

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
    }

    private void initView() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int drawable = 0;
        int content = 0;
        Drawable gradientDrawable = null;
        switch (mTraining.getPlayType()) {
            case Training.PLAY_TYPE_REMOVE:
                mIsHorizontal = false;
                drawable = R.drawable.ic_kline_train;
                content = R.string.remove_train_rule;
                gradientDrawable = createDrawable(new int[]{Color.parseColor("#694FC8"), Color.parseColor("#C86DD7")});
                break;
            case Training.PLAY_TYPE_SORT:
                mIsHorizontal = false;
                drawable = R.drawable.ic_annual_report_train;
                content = R.string.sort_train_rule;
                gradientDrawable = createDrawable(new int[]{Color.parseColor("#FF8930"), Color.parseColor("#F7D34C")});
                break;
            case Training.PLAY_TYPE_MATCH_STAR:
                mIsHorizontal = false;
                drawable = R.drawable.ic_identification_train;
                content = R.string.match_star_train_rule;
                gradientDrawable = createDrawable(new int[]{Color.parseColor("#FE4640"), Color.parseColor("#F69C5D")});
                break;
            case Training.PLAY_TYPE_JUDGEMENT:
                drawable = R.drawable.ic_average_line_train;
                mIsHorizontal = true;
                content = R.string.judgement_train_rule;
                gradientDrawable = createDrawable(new int[]{Color.parseColor("#694FC8"), Color.parseColor("#C86DD7")});
                break;

        }
        if (mIsHorizontal) {
            mVerticalView.setVisibility(View.GONE);
            mHorizontalView.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (drawable != 0) {
                Glide.with(getActivity())
                        .load(drawable)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .priority(Priority.HIGH)
                        .into(mTrainImg);
            }
            if (content != 0) {
                mContent.setText(content);
            }
            mConfirm.setBackground(gradientDrawable);
        } else {
            mVerticalView.setVisibility(View.VISIBLE);
            mHorizontalView.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (drawable != 0) {
                Glide.with(getActivity())
                        .load(drawable)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .priority(Priority.HIGH)
                        .into(mTrainImg1);
            }
            if (content != 0) {
                mContent1.setText(content);
            }
            mConfirm1.setBackground(gradientDrawable);
        }
        // TODO: 2017-08-14 load gif
    }

    private void requestTrainGuide() {

    }

    @OnClick({R.id.confirm, R.id.confirm1})
    public void onViewClicked(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    private Drawable createDrawable(int[] colors) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradient.setCornerRadius(Display.dp2Px(100, getResources()));
        return gradient;
    }
}
