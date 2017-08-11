package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RewardSelectMoneyView extends LinearLayout {
    @BindView(R.id.index0)
    TextView mIndex0;
    @BindView(R.id.index1)
    TextView mIndex1;
    @BindView(R.id.index2)
    TextView mIndex2;
    @BindView(R.id.index3)
    TextView mIndex3;
    private int mSelectedIndex;
    private long mOtherMoney;

    public RewardSelectMoneyView(Context context) {
        super(context);
        init();
    }

    public RewardSelectMoneyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RewardSelectMoneyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_reward_select_money, this, true);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.index0, R.id.index1, R.id.index2, R.id.index3})
    public void onViewClicked(View view) {
        switch (mSelectedIndex) {
            case 0:
                mIndex0.setSelected(false);
                break;
            case 1:
                mIndex1.setSelected(false);
                break;
            case 2:
                mIndex2.setSelected(false);
                break;
            case 3:
                mIndex3.setSelected(false);
            default:
                break;
        }
        switch (view.getId()) {
            case R.id.index0:
                mIndex0.setSelected(true);
                mSelectedIndex = 0;
                break;
            case R.id.index1:
                mIndex1.setSelected(true);
                mSelectedIndex = 1;
                break;
            case R.id.index2:
                mIndex2.setSelected(true);
                mSelectedIndex = 2;
                break;
            case R.id.index3:
                mIndex3.setSelected(true);
                mSelectedIndex = 3;
                break;
        }
    }

    public RewardSelectMoneyView setOtherMoney(long money) {
        mOtherMoney = money;
        mIndex3.setText(getContext().getString(R.string.other) + "\n" + String.valueOf(money));
        return this;
    }

    public void setSelectedIndex(int index) {
        switch (mSelectedIndex) {
            case 0:
                mIndex0.setSelected(false);
                break;
            case 1:
                mIndex1.setSelected(false);
                break;
            case 2:
                mIndex2.setSelected(false);
                break;
            case 3:
                mIndex3.setSelected(false);
            default:
                break;
        }
        mSelectedIndex = index;
        switch (index) {
            case 0:
                mIndex0.setSelected(true);
                break;
            case 1:
                mIndex1.setSelected(true);
                break;
            case 2:
                mIndex2.setSelected(true);
                break;
            case 3:
                mIndex3.setSelected(true);
            default:
                break;
        }
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public long getSelectedMoney() {
        switch (mSelectedIndex) {
            case 0:
                return Long.valueOf(mIndex0.getText().toString());
            case 1:
                return Long.valueOf(mIndex1.getText().toString());
            case 2:
                return Long.valueOf(mIndex2.getText().toString());
            case 3:
                return mOtherMoney;
            default:
                break;
        }
        return 0;
    }
}
