package com.sbai.finance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.HomeTitleView;
import com.sbai.finance.websocket.market.DataReceiveListener;
import com.sbai.finance.websocket.market.MarketSubscribe;
import com.sbai.finance.websocket.market.MarketSubscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\10\26 0026.
 */

public class HomePageFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.homeTitleView)
    HomeTitleView mHomeTitleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startScheduleJob(5*1000);
        MarketSubscriber.get().subscribeAll();
        MarketSubscriber.get().addDataReceiveListener(mDataReceiveListener);
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestStockIndexData();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
        MarketSubscriber.get().removeDataReceiveListener(mDataReceiveListener);
        MarketSubscriber.get().unSubscribeAll();
    }

    private DataReceiveListener mDataReceiveListener = new DataReceiveListener<Resp<FutureData>>() {
        @Override
        public void onDataReceive(Resp<FutureData> data) {
            if (data.getCode() == MarketSubscribe.REQ_QUOTA && data.hasData()) {
                mHomeTitleView.putNewFutureData(data.getData());
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeTitleView.setIndexClickListener(new HomeTitleView.IndexClickListener() {
            @Override
            public void onStockClick() {
                requestStockIndexData();
            }

            @Override
            public void onFutureClick() {
                requestFutureVarietyList();
            }

            @Override
            public void onSelectClick() {
                requestOptionalData();
            }
        });
        mHomeTitleView.clickIndexButton(HomeTitleView.BUTTON_HUSHEN);
        requestStockIndexData();
    }

    private void requestStockIndexData() {
        Client.getStockIndexVariety().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        mHomeTitleView.updateStockIndexData(data);
                        requestStockIndexMarketData(data);
                    }
                }).fire();
    }

    private void requestStockIndexMarketData(List<Variety> data){
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getVarietyType()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getStockMarketData(stringBuilder.toString())
                .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {
                    @Override
                    protected void onRespSuccessData(List<StockData> result) {
                        mHomeTitleView.updateStockOrSelectData(result);
//                        mHomeTitleView.updateStockIndexMarketData(result);
                    }
                }).fireFree();
    }

    public void requestFutureVarietyList() {
        //这里只需要3个数据，所以请求的page = 0;
        int page = 0;
        Client.getVarietyList(Variety.VAR_FUTURE, page, Variety.FUTURE_FOREIGN).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        mHomeTitleView.updateFutureData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
//                        stopRefreshAnimation();
                    }

                }).fireFree();
    }

    private void requestOptionalData() {
        //这里只需要3个数据，所以请求的page = 0;
        int page = 0;
        Client.getOptional(page).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        mHomeTitleView.updateSelectData(data);
                        updateOptionInfo((ArrayList<Variety>) data);
                    }
                }).fireFree();
    }

    private void updateOptionInfo(ArrayList<Variety> data) {
        requestMarketData(data);
    }

    private void requestMarketData(ArrayList<Variety> data) {
        List<Variety> futures = new ArrayList<>();
        List<Variety> stocks = new ArrayList<>();
        for (Variety variety : data) {
            if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                stocks.add(variety);
            } else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                futures.add(variety);
            }
        }
//        requestFutureMarketData(futures);
        requestStockIndexMarketData(stocks);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
