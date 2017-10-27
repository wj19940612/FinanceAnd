package com.sbai.finance.fragment.battle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureVersus;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BattleListFragment extends BaseFragment {
    private static final String BATTLE_TYPE = "column-count";

    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.customSwipeRefreshLayout)
    LinearLayout mCustomSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;


    private Set<Integer> mSet;

    private Unbinder mBind;
    private BattleListActivity.VersusListAdapter mVersusListAdapter;

    public BattleListFragment() {
    }

    @SuppressWarnings("unused")
    public static BattleListFragment newInstance(int columnCount) {
        BattleListFragment fragment = new BattleListFragment();
        Bundle args = new Bundle();
        args.putInt(BATTLE_TYPE, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        mVersusListAdapter = new BattleListActivity.VersusListAdapter(getActivity());
//        mListView.setAdapter(mVersusListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSet.clear();
        requestArenaBattleList();
    }

    private void requestArenaBattleList() {
        // TODO: 2017/10/26 请求竞技场对战数据
        Client.getVersusGaming(null).setTag(TAG)
                .setCallback(new Callback2D<Resp<FutureVersus>, FutureVersus>() {
                    @Override
                    protected void onRespSuccessData(FutureVersus data) {
                        updateVersusData(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                }).fireFree();
    }

    private void updateVersusData(FutureVersus futureVersus) {
        if (mSet.isEmpty()) {
            mVersusListAdapter.clear();
        }

        for (Battle battle : futureVersus.getList()) {
//            if (mSet.add(battle.getId())) {
            mVersusListAdapter.add(battle);
//            }
        }
        if (!futureVersus.hasMore()) {
//            mCustomSwipeRefreshLayout.setLoadMoreEnable(false);
        } else if (futureVersus.getList().size() > 0) {
//            mLocation = futureVersus.getList().get(futureVersus.getList().size() - 1).getCreateTime();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
