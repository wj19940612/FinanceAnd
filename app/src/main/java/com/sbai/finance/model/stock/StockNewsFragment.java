package com.sbai.finance.model.stock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class StockNewsFragment extends Fragment {

    private static final String KEY_STOCK_CODE = "stock_code";
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    private String mStockCode;
    private int mPage;

    private Unbinder mBind;

    public StockNewsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_opinion, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestCompanyAnnualReport(0);
    }

    public void requestCompanyAnnualReport(final int page) {
        this.mPage = page;
        Client.getCompanyAnnualReport(mStockCode, mPage, Client.DEFAULT_PAGE_SIZE, CompanyAnnualReportModel.TYPE_STOCK_NEWS)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<StockNewsModel>>, ArrayList<StockNewsModel>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<StockNewsModel> data) {
                        for (StockNewsModel model : data) {
                            Log.d(TAG, "onRespSuccessData: 年报  " + model.toString());
                        }
                        updateStockNewsList(data, page);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }
                })
                .fire();
    }

    private void updateStockNewsList(ArrayList<StockNewsModel> data, int page) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
