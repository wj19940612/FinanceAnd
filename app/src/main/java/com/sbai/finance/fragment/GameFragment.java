package com.sbai.finance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.battle.MoneyRewardGameBattleListActivity;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/10/24.
 */

public class GameFragment extends BaseFragment {
    @BindView(R.id.moneyRewardGame)
    AppCompatButton mMoneyRewardGame;
    @BindView(R.id.ordinaryBattle)
    AppCompatButton mOrdinaryBattle;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.moneyRewardGame, R.id.ordinaryBattle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.moneyRewardGame:
                Launcher.with(getActivity(), MoneyRewardGameBattleListActivity.class).execute();
                break;
            case R.id.ordinaryBattle:
                Launcher.with(getActivity(), BattleListActivity.class).execute();
                break;
        }
    }
}
