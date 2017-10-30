package com.sbai.finance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.Banner;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.model.leaderboard.LeaderThreeRank;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.BusinessBanner;
import com.sbai.finance.view.HomeBanner;
import com.sbai.finance.view.HomeTitleView;
import com.sbai.finance.view.ImportantNewsView;
import com.sbai.finance.view.LeaderBoardView;
import com.sbai.finance.view.SevenHourNewsView;
import com.sbai.finance.websocket.market.DataReceiveListener;
import com.sbai.finance.websocket.market.MarketSubscribe;
import com.sbai.finance.websocket.market.MarketSubscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.model.leaderboard.LeaderThreeRank.INGOT;
import static com.sbai.finance.model.leaderboard.LeaderThreeRank.PROFIT;
import static com.sbai.finance.model.leaderboard.LeaderThreeRank.SAVANT;

/**
 * Created by Administrator on 2017\10\26 0026.
 */

public class HomePageFragment extends BaseFragment {
    public static final int BANNER_TYPE_BANNER = 0;
    public static final int BANNER_TYPE_BUSINESS_BANNER = 1;
    Unbinder unbinder;
    @BindView(R.id.homeTitleView)
    HomeTitleView mHomeTitleView;
    @BindView(R.id.banner)
    HomeBanner mBanner;
    @BindView(R.id.businessBanner)
    BusinessBanner mBusinessBanner;
    @BindView(R.id.leaderBoardView)
    LeaderBoardView mLeaderBoardView;
    @BindView(R.id.sevenHourNewsView)
    SevenHourNewsView mSevenHourNewsView;
    @BindView(R.id.importantNewsView)
    ImportantNewsView mImportantNewsView;


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
        startScheduleJob(5 * 1000);
        MarketSubscriber.get().subscribeAll();
        MarketSubscriber.get().addDataReceiveListener(mDataReceiveListener);
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestStockIndexData();
        mBanner.nextAdvertisement();
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

        mBanner.setOnViewClickListener(new HomeBanner.OnViewClickListener() {
            @Override
            public void onBannerClick(Banner information) {
                if (information.isH5Style()) {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_URL, information.getContent())
                            .execute();
                } else {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_HTML, information.getContent())
                            .putExtra(WebActivity.EX_TITLE, information.getTitle())
                            .execute();
                }
            }
        });
        mBusinessBanner.setOnViewClickListener(new HomeBanner.OnViewClickListener() {
            @Override
            public void onBannerClick(Banner information) {
                if (information.isH5Style()) {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_URL, information.getContent())
                            .execute();
                } else {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_HTML, information.getContent())
                            .putExtra(WebActivity.EX_TITLE, information.getTitle())
                            .execute();
                }
            }
        });
        mLeaderBoardView.setLookRankListener(new LeaderBoardView.LookRankListener() {
            @Override
            public void lookRank(String rankType) {
                if (rankType.equals(INGOT)) {

                } else if (rankType.equals(PROFIT)) {

                } else if (rankType.equals(SAVANT)) {

                }
            }
        });
        mLeaderBoardView.setMobaiListener(new LeaderBoardView.MobaiListener() {
            @Override
            public void mobai(String rankType, LeaderThreeRank item) {
                if (item.getUser() != null) {
                    if (LocalUser.getUser().isLogin()) {
                        Log.e("zzz Home","click mobai");
                        requestWorship(rankType, item.getUser().getId());
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            }
        });
        requestBannerData();
        requestBusniessBannerData();
        requestLeaderBoardData();
        request7NewsData();
        requestImportantNewsData();
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

    private void requestStockIndexMarketData(List<Variety> data) {
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

    private void requestBannerData() {
        Client.getHomeBannerData(BANNER_TYPE_BANNER).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Banner>>, List<Banner>>() {
                    @Override
                    protected void onRespSuccessData(List<Banner> data) {
                        mBanner.setHomeAdvertisement(data);
                    }
                }).fireFree();
    }

    private void requestBusniessBannerData() {
        Client.getHomeBannerData(BANNER_TYPE_BUSINESS_BANNER).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Banner>>, List<Banner>>() {
                    @Override
                    protected void onRespSuccessData(List<Banner> data) {
                        mBusinessBanner.setBusinessBannerData(data);
                    }
                }).fireFree();
    }

    private void requestLeaderBoardData() {
        Client.getleaderBoardThree().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<LeaderThreeRank>>,List<LeaderThreeRank>>() {
                    @Override
                    protected void onRespSuccessData(List<LeaderThreeRank> data) {
                        mLeaderBoardView.updateLeaderBoardData(data);
                    }
                }).fireFree();
    }

    private void request7NewsData(){
        int type = 1;//1为资讯
        Client.getDailyReport(type).setTag(TAG).setCallback(new Callback2D<Resp<List<DailyReport>>,List<DailyReport>>() {
            @Override
            protected void onRespSuccessData(List<DailyReport> data) {
                mSevenHourNewsView.setTextNews(data);
            }
        }).fireFree();
    }

    private void requestImportantNewsData(){
        int type = 2;//2为要闻
        Client.getDailyReport(type).setTag(TAG).setCallback(new Callback2D<Resp<List<DailyReport>>,List<DailyReport>>() {
            @Override
            protected void onRespSuccessData(List<DailyReport> data) {
                mImportantNewsView.setImportantNews(data);
            }
        }).fireFree();
    }

    private void requestWorship(String type, int id) {
        Client.worship(id, type, null).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            requestLeaderBoardData();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
