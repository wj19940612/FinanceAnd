package com.sbai.finance.fragment.future;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.versus.VersusTrade;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
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

    int battleID;

    public static FutureBattleDetailFragment newInstance(int battleID) {
        FutureBattleDetailFragment detailFragment = new FutureBattleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("battleID", battleID);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mListview.setVerticalScrollBarEnabled(false);
        mListview.setDivider(null);
        mListview.setAdapter(mBattleTradeAdapter);
        mListview.setBackgroundResource(R.drawable.ic_futures_versus_bg);
        requestBattleDetail();
    }

    private void requestBattleDetail() {
        fillTestData();
    }

    private void fillTestData() {
        List<VersusTrade> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            VersusTrade item = new VersusTrade();
            item.setInfo("88.88买多建仓");
            item.setTime("12:44");
            list.add(item);
        }
        mBattleTradeAdapter.addAll(list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
