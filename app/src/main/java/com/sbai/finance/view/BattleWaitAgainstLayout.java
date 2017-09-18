package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 期货对战创建后，等待人加入的  快速匹配 邀请好友 取消对战3个按钮
 */

public class BattleWaitAgainstLayout extends LinearLayout {

    @BindView(R.id.invite)
    Button mInvite;
    @BindView(R.id.match)
    Button mMatch;
    @BindView(R.id.cancel)
    Button mCancel;
    @BindView(R.id.countdown)
    TextView mCountdown;

    private OnViewClickListener mOnViewClickListener;

    public interface OnViewClickListener {
        void onInviteFriendClick();

        void onQuickMatchClick();

        void onCancelBattleClick();
    }

    public BattleWaitAgainstLayout(Context context) {
        super(context);
        init();
    }

    public BattleWaitAgainstLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BattleWaitAgainstLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_battle_buttons, this, true);
        ButterKnife.bind(this);

        mInvite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onInviteFriendClick();
                }
            }
        });

        mMatch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onQuickMatchClick();
                }
            }
        });

        mCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onCancelBattleClick();
                }
            }
        });
    }

    public void updateCountDownTime(String time) {
        mCountdown.setText(getContext().getString(R.string.count_down, time));
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetLayoutParams();
    }

    private void resetLayoutParams() {
        LinearLayout.LayoutParams params = (LayoutParams) mInvite.getLayoutParams();
        params.width = mInvite.getMeasuredHeight() * 640 / 176;
        mInvite.setLayoutParams(params);

        mMatch.setLayoutParams(params);

        mCancel.setLayoutParams(params);
    }
}
