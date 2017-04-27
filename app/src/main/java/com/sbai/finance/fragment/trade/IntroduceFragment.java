package com.sbai.finance.fragment.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IntroduceFragment extends BaseFragment {

    @BindView(R.id.tradeCategory)
    TextView mTradeCategory;
    @BindView(R.id.tradeCode)
    TextView mTradeCode;
    @BindView(R.id.tradeTimeSummerWinter)
    TextView mTradeTimeSummerWinter;
    @BindView(R.id.holdingTime)
    TextView mHoldingTime;
    @BindView(R.id.tradeUnit)
    TextView mTradeUnit;
    @BindView(R.id.quoteUnit)
    TextView mQuoteUnit;
    @BindView(R.id.lowestMargin)
    TextView mLowestMargin;
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.tradeSystem)
    TextView mTradeSystem;
    @BindView(R.id.deliveryTime)
    TextView mDeliveryTime;
    @BindView(R.id.dailyPriceMaximumVolatilityLimit)
    TextView mDailyPriceMaximumVolatilityLimit;
    Unbinder unbinder;

    Variety mVariety;

    public static IntroduceFragment newInstance(Bundle args) {
        IntroduceFragment fragment = new IntroduceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduce, container, false);
        unbinder = ButterKnife.bind(this, view);
        mVariety = getArguments().getParcelable(Launcher.EX_PAYLOAD);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
