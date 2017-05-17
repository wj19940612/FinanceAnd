package com.sbai.finance.fragment.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sbai.finance.R;
import com.sbai.finance.model.stock.StockData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PriceLimitRankingFragment extends Fragment {

    private static final String KEY_SORT_TYPE = "sort_type";
    private static final String KEY_STOCK_TYPE = "sort_type";

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;

    private Unbinder mBind;
    private int mSortType;
    private int mStockType;

    public PriceLimitRankingFragment() {
    }

    public static PriceLimitRankingFragment newInstance(int sort_type, int stock_type) {
        PriceLimitRankingFragment fragment = new PriceLimitRankingFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_SORT_TYPE, sort_type);
        args.putInt(KEY_STOCK_TYPE, stock_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSortType = getArguments().getInt(KEY_SORT_TYPE);
            mStockType = getArguments().getInt(KEY_STOCK_TYPE);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setEmptyView(mEmpty);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class StockSortAdapter extends ArrayAdapter<StockData> {

        private Context mContext;

        public StockSortAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }
}
