package com.sbai.finance.activity.stock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.future.FutureListFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-19.
 */

public class StockListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,CustomSwipeRefreshLayout.OnLoadMoreListener {

    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.marketArea)
    LinearLayout mMarketArea;
    @BindView(R.id.shangHai)
    TextView mShangHai;
    @BindView(R.id.shenZhen)
    TextView mShenZhen;
    @BindView(R.id.board)
    TextView mBoard;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.stock)
    EditText mStock;
    @BindView(R.id.search)
    ImageView mSearch;

    private FutureListFragment.FutureListAdapter mListAdapter;

    private Integer mPage = 0;
    private Integer mPageSize = 15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);
        ButterKnife.bind(this);
        initView();

        requestStockData();
        requestStockIndexData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        mStock.setFocusable(false);
        mListAdapter = new FutureListFragment.FutureListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setEmptyView(mEmpty);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapte(mListView,mListAdapter);

        //测试数据 后期删除
        SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor("上证",
                "\n" + "24396.26", "\n50.39 +0.21%", 1.133f, 0.667f,
                ContextCompat.getColor(this, R.color.redPrimary),
                ContextCompat.getColor(this, R.color.redPrimary));
        mShangHai.setText(attentionSpannableString);
        mShenZhen.setText(attentionSpannableString);
        mBoard.setText(attentionSpannableString);
    }

    private void requestStockData() {
        //获取股票列表
        Client.getStockVariety(mPage,mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>,List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateStockData((ArrayList<Variety>) data);
                    }
                }).fire();
    }

    private void requestStockIndexData() {
        //获取股票指数
        Client.getStockIndexVariety().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>,List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateStockIndexData((ArrayList<Variety>) data);
                    }
                }).fire();
    }

    private void updateStockIndexData(ArrayList<Variety> data) {
        mShangHai.setText(getSpannableStringByData(data, getString(R.string.ShangHaiStockExchange)));
        mShenZhen.setText(getSpannableStringByData(data, getString(R.string.ShenzhenStockExchange)));
        mBoard.setText(getSpannableStringByData(data, getString(R.string.GrowthEnterpriseMarket)));
    }

    // TODO: 2017/5/3 这边还没做完
    private SpannableString getSpannableStringByData(ArrayList<Variety> data, String market) {
       //1. 获取当前Variety
        Variety variety = null;
        // 2.判断涨跌
        int s2Color = R.color.redPrimary;
        int s3Color = R.color.redPrimary;
        // 3.生成SpannableString
        SpannableString spannableString = StrUtil.mergeTextWithRatioColor(market,
                "\n" + "24396.26", "\n50.39 +0.21%", 1.133f, 0.667f,
                ContextCompat.getColor(this, s2Color),
                ContextCompat.getColor(this, s3Color));
        return spannableString;
    }


    private void updateStockData(ArrayList<Variety> data) {
        if (data == null){
            return;
        }
        stopRefreshAnimation();
        mListAdapter.addAll(data);
        if (data.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }
        mListAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.stock, R.id.search, R.id.marketArea})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stock:
            case R.id.search:
                Launcher.with(getActivity(), SearchStockActivity.class).execute();
                break;
            case R.id.marketArea:
                // TODO: 2017/5/3 跳转指数详情
                break;
        }
    }

    @Override
    public void onRefresh() {
        reset();
        requestStockData();
    }

    private void reset() {
        mPage = 0;
        mListAdapter.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    @Override
    public void onLoadMore() {
         requestStockData();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }
}
