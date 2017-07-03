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
 * Created by linrongfang on 2017/6/19.
 */

public class BattleButtons extends LinearLayout {

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
        void onInviteButtonClick();

        void onMatchButtonClick();

        void onCancelButtonClick();
    }

    public BattleButtons(Context context) {
        super(context);
        init();
    }

    public BattleButtons(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BattleButtons(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_buttons, null, false);
        addView(view);
        ButterKnife.bind(this);

        mInvite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onInviteButtonClick();
                }
            }
        });

        mMatch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onMatchButtonClick();
                }
            }
        });

        mCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onCancelButtonClick();
                }
            }
        });
    }

    public void updateCountDownTime(String time) {
        mCountdown.setText("倒计时" + time);
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

}
