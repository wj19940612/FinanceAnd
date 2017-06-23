package com.sbai.finance.fragment.future;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.versus.TradeRecord;
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by linrongfang on 2017/6/20.
 */

public class FutureBattleDetailFragment extends BaseFragment {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listview)
    ListView mListview;
    Unbinder unbinder;

    BattleTradeView.BattleTradeAdapter mBattleTradeAdapter;

    private VersusGaming mVersusGaming;

    public static FutureBattleDetailFragment newInstance(VersusGaming versusGaming) {
        FutureBattleDetailFragment detailFragment = new FutureBattleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("versusGaming", versusGaming);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVersusGaming = (VersusGaming) getArguments().get("versusGaming");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_future_battle_detail, null, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mBattleTradeAdapter = new BattleTradeView.BattleTradeAdapter(getContext());
        mListview.setAdapter(mBattleTradeAdapter);
        requestOrderHistory();
    }

    private void requestOrderHistory() {
        Client.getOrderHistory(mVersusGaming.getId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<List<TradeRecord>>() {
                    @Override
                    protected void onRespSuccess(List<TradeRecord> resp) {
                        updateTradeHistory(resp);
                    }
                })
                .fire();
    }

    private void updateTradeHistory(List<TradeRecord> resp) {
        mBattleTradeAdapter.setUserId(mVersusGaming.getLaunchUser(), mVersusGaming.getAgainstUser());
        mBattleTradeAdapter.addAll(resp);
        mListview.setSelection(View.FOCUS_DOWN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
