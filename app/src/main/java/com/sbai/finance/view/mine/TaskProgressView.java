package com.sbai.finance.view.mine;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2018\1\2 0002.
 */

public class TaskProgressView extends RelativeLayout {
    @BindView(R.id.firstAward)
    ImageView mFirstAward;
    @BindView(R.id.secondAward)
    ImageView mSecondAward;
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

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_task_center_progress, this, true);
        ButterKnife.bind(this);
        mProgressViews[0] = mFirstProgress;
        mProgressViews[1] = mSecondProgress;
        mProgressViews[2] = mThirdProgress;
        mProgressViews[3] = mFourthProgress;
        mProgressViews[4] = mFifthProgress;
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
}
