package com.sbai.finance.fragment.battle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.TradeRecord;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.BattleFloatView;
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
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.battleView)
    BattleFloatView mBattleView;

    Unbinder unbinder;

    BattleTradeView.BattleTradeAdapter mBattleTradeAdapter;

    private Battle mBattle;

    public static FutureBattleDetailFragment newInstance(Battle battle) {
        FutureBattleDetailFragment detailFragment = new FutureBattleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("battle", battle);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBattle = (Battle) getArguments().get("battle");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_future_battle_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        scrollToTop(mTitleBar, mListView);
        mBattleTradeAdapter = new BattleTradeView.BattleTradeAdapter(getContext());
        mListView.setAdapter(mBattleTradeAdapter);
        mBattleView.setMode(BattleFloatView.Mode.MINE)
                .initWithModel(mBattle)
                .setDeadline(mBattle.getGameStatus(), 0)
                .setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false)
                .setWinResult(mBattle.getWinResult());

        requestOrderHistory();
    }

    private void requestOrderHistory() {
        Client.getOrderHistory(mBattle.getId())
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeRecord>>, List<TradeRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeRecord> data) {
                        updateTradeHistory(data);
                    }
                })
                .fire();
    }

    private void updateTradeHistory(List<TradeRecord> resp) {
        mBattleTradeAdapter.setUserId(mBattle.getLaunchUser(), mBattle.getAgainstUser());
        mBattleTradeAdapter.setRecordList(resp);
        mListView.setSelection(View.FOCUS_DOWN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
