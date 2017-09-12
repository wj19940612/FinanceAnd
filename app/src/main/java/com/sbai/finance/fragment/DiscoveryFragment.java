package com.sbai.finance.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.discovery.DailyReportActivity;
import com.sbai.finance.activity.discovery.DailyReportDetailActivity;
import com.sbai.finance.activity.future.FuturesListActivity;
import com.sbai.finance.activity.home.OptionalActivity;
import com.sbai.finance.activity.leaderboard.LeaderBoardsActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockListActivity;
import com.sbai.finance.activity.training.MoreTrainFeedbackActivity;
import com.sbai.finance.activity.training.TrainingDetailActivity;
import com.sbai.finance.model.Banner;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.MyTrainingRecord;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ViewGroupUtil;
import com.sbai.finance.view.FeaturesNavigation;
import com.sbai.finance.view.HomeBanner;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.TitleBar;
import com.sbai.httplib.CookieManger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DiscoveryFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.featuresNavigation)
    FeaturesNavigation mFeaturesNavigation;
    @BindView(R.id.futureBattle)
    ImageView mFutureBattle;
    @BindView(R.id.training)
    IconTextRow mTraining;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.daily)
    IconTextRow mDaily;
    @BindView(R.id.image1)
    ImageView mImage1;
    @BindView(R.id.title1)
    TextView mTitle1;
    @BindView(R.id.image2)
    ImageView mImage2;
    @BindView(R.id.title2)
    TextView mTitle2;
    @BindView(R.id.image3)
    ImageView mImage3;
    @BindView(R.id.title3)
    TextView mTitle3;
    @BindView(R.id.click)
    TextView mClick;
    @BindView(R.id.daily1)
    RelativeLayout mDaily1;
    @BindView(R.id.daily2)
    FrameLayout mDaily2;
    @BindView(R.id.daily3)
    FrameLayout mDaily3;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.banner)
    HomeBanner mBanner;

    private TrainAdapter mTrainAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            requestTrainingList();
            requestDailyReportData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestTrainingList();
        requestDailyReportData();
        startScheduleJob(3 * 1000);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBanner.setOnViewClickListener(new HomeBanner.OnViewClickListener() {
            @Override
            public void onBannerClick(Banner information) {
                if (information.isH5Style()) {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_URL, information.getContent())
                            .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                } else {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_HTML, information.getContent())
                            .putExtra(WebActivity.EX_TITLE, information.getTitle())
                            .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                }
            }
        });
        mFeaturesNavigation.setOnNavItemClickListener(new FeaturesNavigation.OnNavItemClickListener() {
            @Override
            public void onOptionalClick() {
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.DISCOVERY_SELF_OPTIONAL);
                    Launcher.with(getActivity(), OptionalActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void onFuturesClick() {
                umengEventCount(UmengCountEventId.DISCOVERY_FUTURES);
                Launcher.with(getActivity(), FuturesListActivity.class).execute();
            }

            @Override
            public void onStockClick() {
                umengEventCount(UmengCountEventId.DISCOVERY_STOCK);
                Launcher.with(getActivity(), StockListActivity.class).execute();
            }

            @Override
            public void onLeaderboardClick() {
                umengEventCount(UmengCountEventId.DISCOVERY_LEADER_BOARD);
                Launcher.with(getActivity(), LeaderBoardsActivity.class).execute();
            }
        });

        scrollToTop(mTitleBar, mScrollView);

        initTrainingListView();
        initDailyReportView();
        requestTrainingList();
        requestDailyReportData();
        requestBannerData();
    }

    private void requestBannerData() {
        Client.getBannerData().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Banner>>, List<Banner>>() {
                    @Override
                    protected void onRespSuccessData(List<Banner> data) {
                        mBanner.setHomeAdvertisement(data);
                    }
                }).fireFree();
    }

    private void initDailyReportView() {
        mDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengEventCount(UmengCountEventId.REPORT_VIEW_LIST);
                Launcher.with(getActivity(), DailyReportActivity.class).execute();
            }
        });
    }

    private void requestTrainingList() {
        Client.getTrainingList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MyTrainingRecord>>, List<MyTrainingRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<MyTrainingRecord> data) {
                        updateTrainData(data);
                    }
                }).fireFree();
    }

    private void updateTrainData(List<MyTrainingRecord> data) {
        mTrainAdapter.clear();
        for (MyTrainingRecord trainingRecord : data) {
            if (trainingRecord.getTrain() != null) {
                mTrainAdapter.add(trainingRecord);
            }
        }
        mTrainAdapter.notifyDataSetChanged();
        ViewGroupUtil.setListViewHeightBasedOnChildren(mListView);
    }

    private void requestDailyReportData() {
        Client.getDailyReport().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<DailyReport>>, List<DailyReport>>() {
                    @Override
                    protected void onRespSuccessData(List<DailyReport> data) {
                        updateReportData(data);
                    }
                }).fireFree();
    }

    private void updateReportData(List<DailyReport> data) {
        if (data.size() > 2) {
            mDaily1.setTag(data.get(0));
            Glide.with(getActivity()).load(data.get(0).getCoverUrl()).into(mImage1);
            mTitle1.setText(data.get(0).getTitle());
            mClick.setText(getString(R.string.read_count, data.get(0).getClicks()));
            mDaily2.setTag(data.get(1));
            Glide.with(getActivity()).load(data.get(1).getCoverUrl()).into(mImage2);
            mTitle2.setText(data.get(1).getTitle());
            mDaily3.setTag(data.get(2));
            Glide.with(getActivity()).load(data.get(2).getCoverUrl()).into(mImage3);
            mTitle3.setText(data.get(2).getTitle());
        }
    }

    private void initTrainingListView() {
        mListView.setFocusable(false);
        mTrainAdapter = new TrainAdapter(getContext());
        mListView.setAdapter(mTrainAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTrainingRecord trainProgram = (MyTrainingRecord) parent.getItemAtPosition(position);
                if (trainProgram != null) {
                    Launcher.with(getActivity(), TrainingDetailActivity.class)
                            .putExtra(ExtraKeys.TRAINING, trainProgram.getTrain())
                            .execute();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        stopScheduleJob();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        mBanner.nextAdvertisement();
    }

    @OnClick({R.id.futureBattle, R.id.training, R.id.daily, R.id.daily1, R.id.daily2, R.id.daily3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.futureBattle:
                Launcher.with(getActivity(), BattleListActivity.class).execute();
                break;
            case R.id.training:
                umengEventCount(UmengCountEventId.DISCOVERY_YOU_WANT_LEARN);
                Launcher.with(getActivity(), MoreTrainFeedbackActivity.class).execute();
                break;
            case R.id.daily:
                break;
            case R.id.daily1:
            case R.id.daily2:
            case R.id.daily3:
                DailyReport dailyReport = (DailyReport) view.getTag();
                if (dailyReport != null) {
                    Launcher.with(getActivity(), DailyReportDetailActivity.class)
                            .putExtra(DailyReportDetailActivity.EX_FORMAT, dailyReport.getFormat())
                            .putExtra(DailyReportDetailActivity.EX_ID, dailyReport.getId())
                            .putExtra(DailyReportDetailActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                }
                break;
        }
    }

    static class TrainAdapter extends ArrayAdapter<MyTrainingRecord> {

        public TrainAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_discover_train, null, true);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.content)
            CardView mContent;
            @BindView(R.id.trainImg)
            ImageView mTrainImg;
            @BindView(R.id.grade)
            TextView mGrade;
            @BindView(R.id.trainType)
            TextView mTrainType;
            @BindView(R.id.trainCount)
            TextView mTrainCount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(MyTrainingRecord item, Context context) {
                Glide.with(context)
                        .load(item.getTrain().getImage2Url())
                        .into(mTrainImg);
                mTrainType.setText(item.getTrain().getTitle());
                if (!LocalUser.getUser().isLogin()) {
                    mTrainCount.setText(context.getString(R.string.train_count, 0));
                } else {
                    if (item.getRecord() == null || item.getRecord().getFinish() == 0) {
                        mTrainCount.setText(context.getString(R.string.have_no_train));
                    } else {
                        mTrainCount.setText(context.getString(R.string.train_count, item.getRecord().getFinish()));
                    }
                }
                mGrade.setText(context.getString(R.string.level, item.getTrain().getLevel()));
                switch (item.getTrain().getType()) {
                    case Training.TYPE_THEORY:
                        mContent.setBackground(createDrawable(new int[]{Color.parseColor("#FFB269"), Color.parseColor("#FB857A")}, context));
                        break;
                    case Training.TYPE_TECHNOLOGY:
                        mContent.setBackground(createDrawable(new int[]{Color.parseColor("#A485FF"), Color.parseColor("#C05DD8")}, context));
                        break;
                    case Training.TYPE_FUNDAMENTAL:
                        mContent.setBackground(createDrawable(new int[]{Color.parseColor("#EEA259"), Color.parseColor("#FDD35E")}, context));
                        break;
                    case Training.TYPE_COMPREHENSIVE:
                        mContent.setBackground(createDrawable(new int[]{Color.parseColor("#64A0FE"), Color.parseColor("#995BF4")}, context));
                        break;
                }
            }

            private Drawable createDrawable(int[] colors, Context context) {
                GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
                gradient.setCornerRadius(Display.dp2Px(8, context.getResources()));
                return gradient;
            }
        }
    }
}
