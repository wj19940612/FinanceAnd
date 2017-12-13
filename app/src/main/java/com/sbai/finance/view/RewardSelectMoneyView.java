package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.view.autofit.AutofitTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RewardSelectMoneyView extends LinearLayout {

    public static final int EXCHANGE_RATE_FOR_CASH_EXCHANGE = 100;  //现金兑换元宝汇率

    @BindView(R.id.firstRewardLL)
    LinearLayout mFirstRewardLL;
    @BindView(R.id.secondRewardLL)
    LinearLayout mSecondRewardLL;
    @BindView(R.id.thirdRewardLL)
    LinearLayout mThirdRewardLL;
    @BindView(R.id.otherIngotNumber)
    AutofitTextView mOtherIngotNumber;
    @BindView(R.id.otherMoneyContent)
    TextView mOtherMoneyContent;
    @BindView(R.id.otherRewardLL)
    LinearLayout mOtherRewardLL;
    private int mSelectedIndex;
    private long mOtherMoney;
    private OnSelectedCallback mOnSelectedCallback;

    public void setOnSelectedCallback(OnSelectedCallback onSelectedCallback) {
        mOnSelectedCallback = onSelectedCallback;
    }

    public interface OnSelectedCallback {
        void selected(long money);

        void selectedOther();
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


        post(new Runnable() {
            @Override
            public void run() {
                mSelectedIndex = 0;
                changeReward(mSelectedIndex, true);
                if (mOnSelectedCallback != null) {
                    mOnSelectedCallback.selected(10);
                }
            }
        });
    }

    @OnClick({R.id.firstRewardLL, R.id.secondRewardLL, R.id.thirdRewardLL, R.id.otherRewardLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.firstRewardLL:
                changeReward(mSelectedIndex, false);
                mSelectedIndex = 0;
                changeReward(mSelectedIndex, true);
                break;
            case R.id.secondRewardLL:
                changeReward(mSelectedIndex, false);
                mSelectedIndex = 1;
                changeReward(mSelectedIndex, true);
                break;
            case R.id.thirdRewardLL:
                changeReward(mSelectedIndex, false);
                mSelectedIndex = 2;
                changeReward(mSelectedIndex, true);
                break;
            case R.id.otherRewardLL:
                if (mOnSelectedCallback != null) {
                    mOnSelectedCallback.selectedOther();
                }
                break;
        }
    }

    private void changeReward(int selectedIndex, boolean isSelect) {
        switch (selectedIndex) {
            case 0:
                mFirstRewardLL.setSelected(isSelect);
                for (int i = 0; i < mFirstRewardLL.getChildCount(); i++) {
                    mFirstRewardLL.getChildAt(i).setSelected(isSelect);
                }
                if (isSelect) {
                    if (mOnSelectedCallback != null) {
                        mOnSelectedCallback.selected(10);
                    }
                }
                break;
            case 1:
                mSecondRewardLL.setSelected(isSelect);
                for (int i = 0; i < mSecondRewardLL.getChildCount(); i++) {
                    mSecondRewardLL.getChildAt(i).setSelected(isSelect);
                }
                if (isSelect) {
                    if (mOnSelectedCallback != null) {
                        mOnSelectedCallback.selected(100);
                    }
                }
                break;
            case 2:
                mThirdRewardLL.setSelected(isSelect);
                for (int i = 0; i < mThirdRewardLL.getChildCount(); i++) {
                    mThirdRewardLL.getChildAt(i).setSelected(isSelect);
                }
                if (isSelect) {
                    if (mOnSelectedCallback != null) {
                        mOnSelectedCallback.selected(1000);
                    }
                }
                break;
            case 3:
                mOtherRewardLL.setSelected(isSelect);
                for (int i = 0; i < mOtherRewardLL.getChildCount(); i++) {
                    mOtherRewardLL.getChildAt(i).setSelected(isSelect);
                }
                break;

        }
    }


    public void setOtherMoney(long money) {
        mOtherMoney = money;
        mOtherMoneyContent.setVisibility(VISIBLE);
        changeReward(mSelectedIndex, false);
        mSelectedIndex = 3;
        changeReward(mSelectedIndex, true);

        mOtherIngotNumber.setText(getContext().getString(R.string.ingot_number_no_blank, money));
        double cash = FinanceUtil.divide(money, EXCHANGE_RATE_FOR_CASH_EXCHANGE).doubleValue();
        mOtherMoneyContent.setText(getContext().getString(R.string.s_yuan, String.valueOf(cash)));
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public long getSelectedMoney() {
        switch (mSelectedIndex) {
            case 0:
                return 10;
            case 1:
                return 100;
            case 2:
                return 1000;
            case 3:
                return mOtherMoney;
            default:
                break;
        }
        return 0;
    }

    public long getOtherMoney() {
        return mOtherMoney;
    }

}
