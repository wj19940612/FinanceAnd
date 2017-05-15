package com.sbai.finance.model.stock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;


public class StockNewsFragment extends Fragment {

    private static final String KEY_STOCK_CODE = "stock_code";
    private String mStockCode;

    public StockNewsFragment() {
        // Required empty public constructor
    }

    public static StockNewsFragment newInstance(String stock_code) {
        StockNewsFragment fragment = new StockNewsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_STOCK_CODE, stock_code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStockCode = getArguments().getString(KEY_STOCK_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock_news, container, false);
    }

}
