package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.miss.RewardMoney;

import java.util.List;

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
    private OnMoneySelectListener mOnMoneySelectListener;
    private int mSelectedIndex;
    private List<RewardMoney> mMoneyList;

    public void setOnMoneySelectListener(OnMoneySelectListener onMoneySelectListener) {
        mOnMoneySelectListener = onMoneySelectListener;
    }

    public interface OnMoneySelectListener {
        void onSelected(int index);
    }

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
        if (mOnMoneySelectListener == null) return;
        switch (view.getId()) {
            case R.id.index0:
                mOnMoneySelectListener.onSelected(0);
                mIndex0.setSelected(true);
                mSelectedIndex = 0;
                break;
            case R.id.index1:
                mOnMoneySelectListener.onSelected(1);
                mIndex1.setSelected(true);
                mSelectedIndex = 1;
                break;
            case R.id.index2:
                mOnMoneySelectListener.onSelected(2);
                mIndex2.setSelected(true);
                mSelectedIndex = 2;
                break;
            case R.id.index3:
                mOnMoneySelectListener.onSelected(3);
                mIndex3.setSelected(true);
                mSelectedIndex = 3;
                break;
        }
    }

    public RewardSelectMoneyView setOtherMoney(long money) {
        mIndex3.setText(getContext().getString(R.string.other) + "\n" + String.valueOf(money));
        return this;
    }

    public RewardSelectMoneyView setMoneyList(List<RewardMoney> moneyList) {
        mMoneyList = moneyList;
        updateMoneyView();
        return this;
    }

    private void updateMoneyView() {
        if (mMoneyList!=null&&mMoneyList.size()>2){
            mIndex0.setText(mMoneyList.get(0).getMoney()+getContext().getString(R.string.ingot));
            mIndex1.setText(mMoneyList.get(1).getMoney()+getContext().getString(R.string.ingot));
            mIndex2.setText(mMoneyList.get(2).getMoney()+getContext().getString(R.string.ingot));
        }
    }
    public void setSelectedIndex(int index){
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
}
