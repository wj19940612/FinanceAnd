package com.sbai.finance.fragment.stock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PriceLimitRankingFragment extends Fragment {


    private Unbinder mBind;

    public PriceLimitRankingFragment() {
    }

    public static PriceLimitRankingFragment newInstance(String param1, String param2) {
        PriceLimitRankingFragment fragment = new PriceLimitRankingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_limit_ranking, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
