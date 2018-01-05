package com.sbai.finance.view.mine;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2018\1\2 0002.
 */

public class TaskProgressView extends RelativeLayout {
    @BindView(R.id.firstAwardLayout)
    RelativeLayout mFirstAwardLayout;
    @BindView(R.id.firstAwardIcon)
    ImageView mFirstAwardIcon;
    @BindView(R.id.firstAwardFlash)
    ImageView mFirstAwardFlash;
    @BindView(R.id.secondAwardLayout)
    RelativeLayout mSecondAwardLayout;
    @BindView(R.id.secondAwardIcon)
    ImageView mSecondAwardIcon;
    @BindView(R.id.secondAwardFlash)
    ImageView mSecondAwardFlash;
    @BindView(R.id.firstProgress)
    View mFirstProgress;
    @BindView(R.id.secondProgress)
    View mSecondProgress;
    @BindView(R.id.thirdProgress)
    View mThirdProgress;
    @BindView(R.id.fourthProgress)
    View mFourthProgress;
    @BindView(R.id.fifthProgress)
    View mFifthProgress;

    public static final int MAX_PROGRESS_NUM = 5;
    private Context mContext;
    private View[] mProgressViews = new View[MAX_PROGRESS_NUM];
    private OnOpenAwardListener mOnOpenAwardListener;

    public TaskProgressView(Context context) {
        this(context, null);
    }

    public TaskProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public interface OnOpenAwardListener {
        public void onOpenAward(boolean first);
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_task_center_progress, this, true);
        ButterKnife.bind(this);
        mProgressViews[0] = mFirstProgress;
        mProgressViews[1] = mSecondProgress;
        mProgressViews[2] = mThirdProgress;
        mProgressViews[3] = mFourthProgress;
        mProgressViews[4] = mFifthProgress;
    }

    public void setOnOpenAwardListener(OnOpenAwardListener onOpenAwardListener) {
        mOnOpenAwardListener = onOpenAwardListener;
    }

    public void setProgress(int progress) {
        if (progress > MAX_PROGRESS_NUM) {
            progress = MAX_PROGRESS_NUM;
        }
        for (int i = 0; i < progress; i++) {
            if (i == 0) {
                mProgressViews[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_task_left_progress));
            } else if (i == MAX_PROGRESS_NUM - 1) {
                mProgressViews[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_task_right_progress));
            } else {
                mProgressViews[i].setBackgroundColor(ContextCompat.getColor(mContext, R.color.taskProgress));
            }
        }

        for (int i = progress; i < MAX_PROGRESS_NUM; i++) {
            if (i == 0) {
                mProgressViews[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_task_left_progress_not));
            } else if (i == MAX_PROGRESS_NUM - 1) {
                mProgressViews[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_task_right_progress_not));
            } else {
                mProgressViews[i].setBackgroundColor(ContextCompat.getColor(mContext, R.color.taskProgressNot));
            }
        }
    }

    //达到第一个礼物目标，还未领取
    public void flashIcon(final boolean first) {
        ImageView icon;
        ImageView flash;
        if (first) {
            icon = mFirstAwardIcon;
            flash = mFirstAwardFlash;
        } else {
            icon = mSecondAwardIcon;
            flash = mSecondAwardFlash;
        }
        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_award));
        flash.setVisibility(View.VISIBLE);
        flash.startAnimation(getFlashAnim());
        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openIcon(first);
                mOnOpenAwardListener.onOpenAward(first);
            }
        });
    }

    //打开第一个礼物
    public void openIcon(boolean first) {
        ImageView icon;
        ImageView flash;
        if (first) {
            icon = mFirstAwardIcon;
            flash = mFirstAwardFlash;
        } else {
            icon = mSecondAwardIcon;
            flash = mSecondAwardFlash;
        }
        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_award_open));
        icon.setOnClickListener(null);
        flash.clearAnimation();
        flash.setVisibility(View.GONE);
    }

    //还未到达能拿的礼物
    public void setNotArriveAwardIcon(boolean first) {
        ImageView icon;
        ImageView flash;
        if (first) {
            icon = mFirstAwardIcon;
            flash = mFirstAwardFlash;
        } else {
            icon = mSecondAwardIcon;
            flash = mSecondAwardFlash;
        }
        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_award_not));
        icon.setOnClickListener(null);
        flash.clearAnimation();
        flash.setVisibility(View.GONE);
    }

    private Animation getFlashAnim() {
        Animation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2500);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        return animation;
    }
}
