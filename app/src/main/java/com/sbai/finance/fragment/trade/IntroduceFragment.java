package com.sbai.finance.fragment.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.FutureIntroduce;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
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

    public static IntroduceFragment newInstance(Variety variety) {
        IntroduceFragment fragment = new IntroduceFragment();
        Bundle args = new Bundle();
        args.putParcelable(Launcher.EX_PAYLOAD, variety);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVariety = getArguments().getParcelable(Launcher.EX_PAYLOAD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduce, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestVarietyTradeIntroduce();
    }

    private void requestVarietyTradeIntroduce() {
        Client.getVarietyTradeIntroduce(mVariety.getVarietyId())
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<FutureIntroduce>, FutureIntroduce>() {
                    @Override
                    protected void onRespSuccessData(FutureIntroduce data) {
                        updateFutureIntroduce(data);
                    }
                })
                .fire();
    }

    private void updateFutureIntroduce(FutureIntroduce data) {
        mTradeCategory.setText(data.getVarietyName());
        mTradeCode.setText(String.valueOf(data.getVarietyType()));
        mTradeTimeSummerWinter.setText(data.getTradeTime());
        mHoldingTime.setText(data.getTradeTime());
        mTradeUnit.setText(data.getTradeUnit());
        mQuoteUnit.setText(data.getReportPriceUnit());
        mLowestMargin.setText(data.getLowestMargin());
        mTradeType.setText(data.getTradeType());
        mTradeSystem.setText(data.getTradeRegime());
        mDeliveryTime.setText(data.getDeliveryTime());
        mDailyPriceMaximumVolatilityLimit.setText(data.getEverydayPriceMaxFluctuateLimit());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
