package com.sbai.finance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.activity.future.FuturesListActivity;
import com.sbai.finance.activity.home.OptionalActivity;
import com.sbai.finance.activity.leaderboard.LeaderBoardsActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockListActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.FeaturesNavigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DiscoveryFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.featuresNavigation)
    FeaturesNavigation mFeaturesNavigation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFeaturesNavigation.setOnNavItemClickListener(new FeaturesNavigation.OnNavItemClickListener() {
            @Override
            public void onOptionalClick() {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), OptionalActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void onFuturesClick() {
                Launcher.with(getActivity(), FuturesListActivity.class).execute();
            }

            @Override
            public void onStockClick() {
                Launcher.with(getActivity(), StockListActivity.class).execute();
            }

            @Override
            public void onLeaderboardClick() {
                Launcher.with(getActivity(), LeaderBoardsActivity.class).execute();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
