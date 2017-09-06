package com.sbai.finance.fragment.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;


public class AccountFundFragment extends BaseFragment {

    private static final String FUND_TYPE = "fund_type";

    public static final int FUND_TYPE_INGOT = 0;
    public static final int FUND_TYPE_CASH = 1;
    public static final int FUND_TYPE_SCORE = 2;

    private int mFundType;

    public AccountFundFragment() {
    }

    /**
     * @param fundType 资金类型
     * @return
     */
    public static AccountFundFragment newInstance(int fundType) {
        AccountFundFragment fragment = new AccountFundFragment();
        Bundle args = new Bundle();
        args.putInt(FUND_TYPE, fundType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFundType = getArguments().getInt(FUND_TYPE, FUND_TYPE_INGOT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_fund, container, false);
    }
}
