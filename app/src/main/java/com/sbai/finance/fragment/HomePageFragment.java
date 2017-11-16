package com.sbai.finance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.home.AllTrainingListActivity;
import com.sbai.finance.activity.home.BroadcastListActivity;
import com.sbai.finance.activity.home.StockFutureActivity;
import com.sbai.finance.activity.leaderboard.LeaderBoardsListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.activity.studyroom.StudyRoomActivity;
import com.sbai.finance.activity.web.DailyReportDetailActivity;
import com.sbai.finance.model.Banner;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.model.Dictum;
import com.sbai.finance.model.Greeting;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.NoticeRadio;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.leaderboard.LeaderThreeRank;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.model.system.Share;
import com.sbai.finance.net.API;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.BusinessBanner;
import com.sbai.finance.view.HomeBanner;
import com.sbai.finance.view.HomeTitleView;
import com.sbai.finance.view.ImportantNewsView;
import com.sbai.finance.view.LeaderBoardView;
import com.sbai.finance.view.SevenHourNewsView;
import com.sbai.finance.view.VerticalScrollTextView;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.httplib.ApiError;
import com.sbai.httplib.CookieManger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.model.leaderboard.LeaderThreeRank.INGOT;
import static com.sbai.finance.model.leaderboard.LeaderThreeRank.PROFIT;
import static com.sbai.finance.model.leaderboard.LeaderThreeRank.SAVANT;
import static com.sbai.finance.view.HomeTitleView.BUTTON_HUSHEN;
import static com.sbai.finance.view.HomeTitleView.BUTTON_QIHUO;
import static com.sbai.finance.view.HomeTitleView.BUTTON_ZIXUAN;

/**
 * Created by Administrator on 2017\10\26 0026.
 */

public class HomePageFragment extends BaseFragment {
    public static final int BANNER_TYPE_BANNER = 0;
    public static final int BANNER_TYPE_BUSINESS_BANNER = 1;
    public static final int TIME_ONE = 1000;//1次轮询为1s
    public static final int TIME_HANDLER_ONE = 1;
    public static final int TIME_HANDLER_THREE = 3;//3次轮询时间
    public static final int TIME_HANDLER_TEN = 10;
    public static final int TIME_HANDLER_FIVE = 5;

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

    private MoreClickListener mMoreClickListener;

    public void setMoreClickListener(MoreClickListener moreClickListener) {
        moreClickListener = moreClickListener;
    }

    public interface MoreClickListener {
        public void onMoreClick(int pageSize);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onTimeUp(int count) {
        if (count % TIME_HANDLER_THREE == 0) {
            mBanner.nextAdvertisement();
        } else if (count % TIME_HANDLER_TEN == 0) {
            request7NewsData();
        } else if (count % TIME_HANDLER_FIVE == 0) {
            getIndexData();
//            requestImportantNewsData();
            requestBusniessBannerData();
            requestLeaderBoardData();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeTitleView.setOnBroadcastListener(new VerticalScrollTextView.OnItemClickListener() {
            @Override
            public void onItemClick(NoticeRadio noticeRadio) {
                umengEventCount(UmengCountEventId.PAGE_BROADCAST);
                Launcher.with(getActivity(), BroadcastListActivity.class).execute();
            }
        });
        mHomeTitleView.setOnDictumClickListener(new HomeTitleView.OnDictumClickListener() {
            @Override
            public void onDictumClick(Dictum dictum) {
                //点击名言

            }
        });
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
        mHomeTitleView.setOnClickItemListener(new HomeTitleView.OnClickItemListener() {
            @Override
            public void onItemClick(int button, Variety variety) {
                if (button == BUTTON_HUSHEN) {
                    umengEventCount(UmengCountEventId.PAGE_HU_SHEN);
                    Launcher.with(getActivity(), StockIndexActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                } else if (button == BUTTON_QIHUO) {
                    umengEventCount(UmengCountEventId.PAGE_FUTURE);
                    Launcher.with(getActivity(), FutureTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();

                } else if (button == BUTTON_ZIXUAN) {
                    umengEventCount(UmengCountEventId.PAGE_OPTIONAL);
                    if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                        Launcher.with(getActivity(), FutureTradeActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                    }
                    if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                        if (variety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.STOCK_EXPONENT)) {
                            Launcher.with(getActivity(), StockIndexActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                        } else {
                            Launcher.with(getActivity(), StockDetailActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                        }
                    }
                }
            }
        });
        mHomeTitleView.setOnLookAllClickListener(new HomeTitleView.OnLookAllClickListener() {
            @Override
            public void onLookAll() {
                //查看更多
                int pageIndex = mHomeTitleView.getOldButton() - 1;
                if (pageIndex == 0) {
                    umengEventCount(UmengCountEventId.PAGE_HU_SHEN_ALL);
                } else if (pageIndex == 1) {
                    umengEventCount(UmengCountEventId.PAGE_FUTURE_ALL);
                }
                Launcher.with(getActivity(), StockFutureActivity.class)
                        .putExtra(ExtraKeys.PAGE_INDEX, pageIndex)
                        .execute();
            }

            @Override
            public void onSelectClick() {
                //自选点击
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.PAGE_OPTIONAL_ALL);
                    Launcher.with(getActivity(), StockFutureActivity.class)
                            .putExtra(ExtraKeys.PAGE_INDEX, 2)
                            .execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override

            public void onPractice() {
                //点击练一练
                umengEventCount(UmengCountEventId.PAGE_TRAINING);
                Launcher.with(getActivity(), AllTrainingListActivity.class).execute();
            }

            @Override
            public void onDaySubjuect() {
                umengEventCount(UmengCountEventId.PAGE_STUDY_ROOM);
                Launcher.with(getActivity(), StudyRoomActivity.class).execute();
                //点击一日一题
            }
        });
        mBanner.setOnViewClickListener(new HomeBanner.OnViewClickListener()

        {
            @Override
            public void onBannerClick(Banner information) {
                umengEventCount(UmengCountEventId.PAGE_BANNER);
                requestClickBanner(information.getId());
                if (information.isH5Style()) {
                    if (!TextUtils.isEmpty(information.getContent())) {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_URL, information.getContent())
                                .execute();
                    }
                } else {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_HTML, information.getContent())
                            .putExtra(WebActivity.EX_TITLE, information.getTitle())
                            .execute();
                }
            }
        });
        mBusinessBanner.setOnViewClickListener(new BusinessBanner.OnViewClickListener()

        {
            @Override
            public void onBannerClick(Banner information, int index) {
                switch (index) {
                    case 0:
                        umengEventCount(UmengCountEventId.PAGE_OPERATE_1);
                        break;
                    case 1:
                        umengEventCount(UmengCountEventId.PAGE_OPERATE_2);
                        break;
                    case 2:
                        umengEventCount(UmengCountEventId.PAGE_OPERATE_3);
                        break;
                }
                requestClickBanner(information.getId());
                if (information.isH5Style() && !TextUtils.isEmpty(information.getContent())) {
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
        mLeaderBoardView.setLookRankListener(new LeaderBoardView.LookRankListener()

        {
            @Override
            public void lookRank(String rankType) {
                int pageIndex = 0;
                //查看我的排名
                if (rankType.equals(INGOT)) {
                    pageIndex = 0;
                    umengEventCount(UmengCountEventId.FIND_INGOT);
                } else if (rankType.equals(PROFIT)) {
                    pageIndex = 1;
                    umengEventCount(UmengCountEventId.FIND_PROFIT);
                } else if (rankType.equals(SAVANT)) {
                    pageIndex = 2;
                    umengEventCount(UmengCountEventId.FIND_SAVANT);
                }
                Launcher.with(getActivity(), LeaderBoardsListActivity.class)
                        .putExtra(ExtraKeys.PAGE_INDEX, pageIndex)
                        .execute();
            }
        });
        mLeaderBoardView.setMobaiListener(new LeaderBoardView.MobaiListener()

        {
            @Override
            public void mobai(String rankType, LeaderThreeRank item) {
                if (item.getUser() != null) {
                    if (LocalUser.getUser().isLogin()) {
                        requestWorship(rankType, item.getUser().getId());
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            }
        });
        mSevenHourNewsView.setOnMoreBtnClickListener(new SevenHourNewsView.OnMoreBtnClickListener()

        {
            @Override
            public void onMoreClick() {
                umengEventCount(UmengCountEventId.PAGE_INFORMATION_MORE);
                ((MainActivity) getActivity()).switchToInformation(0);
//                Launcher.with(getActivity(), InformationAndFocusNewsActivity.class).execute();
            }
        });

        mSevenHourNewsView.setOnShareClickListener(new SevenHourNewsView.OnShareClickListener() {
            @Override
            public void onShare(DailyReport dailyReport) {
                requestShareData(dailyReport);
            }
        });
        mImportantNewsView.setOnImportantNewsClickListener(new ImportantNewsView.OnImportantNewsClickListener()

        {
            @Override
            public void onItemClick(DailyReport dailyReport) {
                Launcher.with(getActivity(), DailyReportDetailActivity.class)
                        .putExtra(DailyReportDetailActivity.EX_FORMAT, dailyReport.getFormat())
                        .putExtra(DailyReportDetailActivity.EX_ID, dailyReport.getId())
                        .putExtra(DailyReportDetailActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                        .execute();
            }

            @Override
            public void onMoreClick() {
                umengEventCount(UmengCountEventId.PAGE_FOCUS_NEWS_MORE);
                ((MainActivity) getActivity()).switchToInformation(1);
//                Launcher.with(getActivity(), InformationAndFocusNewsActivity.class)
//                        .putExtra(ExtraKeys.PAGE_INDEX, 1)
//                        .execute();
            }
        });

        mHomeTitleView.clickIndexButton(BUTTON_HUSHEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        startScheduleJob(TIME_ONE);
        TAG = this.getClass().getSimpleName() + System.currentTimeMillis();
        requestGreetings();
        getRefreshData();
        requestRadioData();
        requestBannerData();
        requestBusniessBannerData();
        requestLeaderBoardData();
        request7NewsData();
        requestImportantNewsData();
//        MarketSubscriber.get().subscribeAll();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            startScheduleJob(TIME_ONE);
            TAG = this.getClass().getSimpleName() + System.currentTimeMillis();
            requestGreetings();
            getRefreshData();
            requestRadioData();
            requestBannerData();
            requestBusniessBannerData();
            requestLeaderBoardData();
            request7NewsData();
            requestImportantNewsData();
        } else if (!isVisibleToUser && isAdded()) {
            stopScheduleJob();
            mHomeTitleView.stopVerticalSrcoll();
            API.cancel(TAG);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
        mHomeTitleView.stopVerticalSrcoll();
        API.cancel(TAG);
    }

    //只更新部分字符,用于轮询数据
    private void getIndexData() {
        if (mHomeTitleView.getOldButton() == BUTTON_HUSHEN) {
            requestStockIndexData();
        } else if (mHomeTitleView.getOldButton() == BUTTON_QIHUO) {
            requestFutureVarietyList();
        } else if (mHomeTitleView.getOldButton() == HomeTitleView.BUTTON_ZIXUAN) {
            requestOptionalData();
        }
    }

    //强制刷新，按钮界面变化
    private void getRefreshData() {
        if (mHomeTitleView.getOldButton() == BUTTON_HUSHEN) {
            mHomeTitleView.forceRefershUI(BUTTON_HUSHEN);
            requestStockIndexData();
        } else if (mHomeTitleView.getOldButton() == BUTTON_QIHUO) {
            mHomeTitleView.forceRefershUI(BUTTON_QIHUO);
            requestFutureVarietyList();
        } else if (mHomeTitleView.getOldButton() == HomeTitleView.BUTTON_ZIXUAN) {
            mHomeTitleView.forceRefershUI(BUTTON_ZIXUAN);
            requestOptionalData();
        }
    }

    private void requestStockIndexData() {
        Client.getStockIndexVariety().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        if (data != null && data.size() != 0) {
                            mHomeTitleView.updateStockIndexData(data);
                            requestStockIndexMarketData(data);
                        }
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
                        if (result != null && result.size() != 0) {
                            mHomeTitleView.updateStockOrSelectData(result);
                        }
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
                        if (data != null && data.size() != 0) {
                            mHomeTitleView.updateFutureData(data);
                            requestFutureMarketData(data);
                        }
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
//                        stopRefreshAnimation();
                    }

                }).fireFree();
    }

    private void requestFutureMarketData(List<Variety> data) {
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getContractsCode()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getFutureMarketData(stringBuilder.toString()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<FutureData>>, List<FutureData>>() {
                    @Override
                    protected void onRespSuccessData(List<FutureData> data) {
                        mHomeTitleView.putNewFutureData(data);
                    }
                })
                .fireFree();
    }

    private void requestOptionalData() {
        //这里只需要3个数据，所以请求的page = 0;
        if (!LocalUser.getUser().isLogin()) {
            mHomeTitleView.forceInitSelectUI();
            return;
        }
        int page = 0;
        Client.getOptional(page).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        if (data != null) {
                            mHomeTitleView.updateSelectData(data);
                            updateOptionInfo((ArrayList<Variety>) data);
                        }
                    }
                }).fireFree();
    }

    private void updateOptionInfo(ArrayList<Variety> data) {
        if (data != null && data.size() > 0) {
            requestMarketData(data);
        }
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
        requestFutureMarketData(futures);
        requestStockIndexMarketData(stocks);
    }

    private void requestGreetings() {
        if (!LocalUser.getUser().isLogin()) {
            mHomeTitleView.setGreetingTitle(R.string.welcome_lemi);
            return;
        }
        Client.requestGreeting().setTag(TAG).setCallback(new Callback2D<Resp<Greeting>, Greeting>() {
            @Override
            protected void onRespSuccessData(Greeting data) {
                if (!LocalUser.getUser().isLogin()) {
                    mHomeTitleView.setGreetingTitle(R.string.welcome_lemi);
                    return;
                }
                mHomeTitleView.setGreetingTitle(data);
            }
        }).fireFree();
    }

    private void requestRadioData() {
        Client.requestRadioData().setTag(TAG).setCallback(new Callback2D<Resp<List<NoticeRadio>>, List<NoticeRadio>>() {
            @Override
            protected void onRespSuccessData(List<NoticeRadio> data) {
                if (data == null || data.size() == 0) {
                    requestDictum();
                } else {
                    mHomeTitleView.setBroadcastContent(data);
                }
            }
        }).fireFree();
    }

    private void requestDictum() {
        Client.requestfindDictumData().setTag(TAG).setCallback(new Callback2D<Resp<List<Dictum>>, List<Dictum>>() {
            @Override
            protected void onRespSuccessData(List<Dictum> data) {
                if (data != null && data.size() != 0) {
                    mHomeTitleView.setDictum(data);
                }
            }
        }).fireFree();
    }

    private void requestBannerData() {
        Client.getHomeBannerData(BANNER_TYPE_BANNER).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Banner>>, List<Banner>>() {
                    @Override
                    protected void onRespSuccessData(List<Banner> data) {
                        if (data != null) {
                            mBanner.setHomeAdvertisement(data);
                        }
                    }
                }).fireFree();
    }

    private void requestBusniessBannerData() {
        Client.getHomeBannerData(BANNER_TYPE_BUSINESS_BANNER).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Banner>>, List<Banner>>() {
                    @Override
                    protected void onRespSuccessData(List<Banner> data) {
                        if (data != null) {
                            mBusinessBanner.setBusinessBannerData(data);
                        }
                    }
                }).fireFree();
    }

    private void requestLeaderBoardData() {
        Client.getleaderBoardThree().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<LeaderThreeRank>>, List<LeaderThreeRank>>() {
                    @Override
                    protected void onRespSuccessData(List<LeaderThreeRank> data) {
                        if (data != null && data.size() != 0) {
                            mLeaderBoardView.updateLeaderBoardData(data);
                        }
                    }
                }).fireFree();
    }

    private void request7NewsData() {
        Client.getDailyReport(DailyReport.ZIXUN).setTag(TAG).setCallback(new Callback2D<Resp<List<DailyReport>>, List<DailyReport>>() {
            @Override
            protected void onRespSuccessData(List<DailyReport> data) {
                if (data != null && data.size() != 0) {
                    mSevenHourNewsView.setTextNews(data);
                }
            }
        }).fireFree();
    }

    private void requestImportantNewsData() {
        Client.getDailyReport(DailyReport.IMPORTANT).setTag(TAG).setCallback(new Callback2D<Resp<List<DailyReport>>, List<DailyReport>>() {
            @Override
            protected void onRespSuccessData(List<DailyReport> data) {
                if (data != null && data.size() != 0) {
                    mImportantNewsView.setImportantNews(data);
                }
            }
        }).fireFree();
    }

    private void requestWorship(String type, int id) {
        String timeType = LeaderThreeRank.TODAY;
        Client.worship(id, type, timeType).setTag(TAG)
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

                    @Override
                    public void onFailure(ApiError apiError) {
                        requestLeaderBoardData();
                    }
                }).fireFree();
    }

    private void requestClickBanner(String bannerId) {
        Client.requestClickBanner(bannerId).setTag(TAG).fireFree();
    }

    private void requestShareData(final DailyReport dailyReport) {
        Client.requestShareData(Share.SHARE_CODE_INFORMATION).setCallback(new Callback2D<Resp<Share>, Share>() {

            @Override
            protected void onRespSuccessData(Share data) {
                String shareLink = data.getShareLink();
                StringBuilder shareLinkBuilder = new StringBuilder(data.getShareLink());
                if(shareLink.contains("?")){
                    shareLinkBuilder.append("&id=");
                }else{
                    shareLinkBuilder.append("?id=");
                }
                shareLinkBuilder.append(dailyReport.getId());
                ShareDialog.with(getActivity())
                        .setTitle((getActivity().getString(R.string.share_to)))
                        .hasFeedback(false)
                        .setShareThumbUrl(data.getShareLeUrl())
                        .setShareUrl(shareLinkBuilder.toString())
                        .setShareTitle(data.getTitle() == null ? "" : data.getTitle())
                        .setShareDescription(dailyReport.getTitle() + dailyReport.getContent())
                        .show();
            }
        }).fireFree();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
