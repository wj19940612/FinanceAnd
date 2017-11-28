package com.sbai.finance.fragment.trade.stock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.trade.trade.HistoryBusinessActivity;
import com.sbai.finance.activity.trade.trade.TodayBusinessActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 股票成交
 */

public class StockBusinessFragment extends BaseFragment {
    @BindView(R.id.todayBusiness)
    TextView mTodayBusiness;
    @BindView(R.id.historyBusiness)
    TextView mHistoryBusiness;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_business, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.todayBusiness, R.id.historyBusiness})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.todayBusiness:
                // TODO: 2017-11-21
                Launcher.with(getActivity(), TodayBusinessActivity.class).execute();
                break;
            case R.id.historyBusiness:
                Launcher.with(getActivity(), HistoryBusinessActivity.class).execute();
                // TODO: 2017-11-21  
                break;
        }
    }
}
