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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.discovery.DailyReportActivity;
import com.sbai.finance.activity.discovery.DailyReportDetailActivity;
import com.sbai.finance.activity.future.FuturesListActivity;
import com.sbai.finance.activity.home.OptionalActivity;
import com.sbai.finance.activity.leaderboard.LeaderBoardsActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockListActivity;
import com.sbai.finance.activity.studyroom.StudyRoomActivity;
import com.sbai.finance.activity.train.MoreTrainFeedBackActivity;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.train.TrainProgram;
import com.sbai.finance.model.train.Train;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.FeaturesNavigation;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.MyListView;
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
    MyListView mListView;
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
    RelativeLayout mDaily2;
    @BindView(R.id.daily3)
    RelativeLayout mDaily3;

    private TrainAdapter mTrainAdapter;

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

        initTrainListView();
        initDailyReportView();
        requestTrainData();
        requestDailyReportData();
    }

    private void initDailyReportView() {
        mDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), DailyReportActivity.class).execute();
            }
        });
    }

    private void requestTrainData() {
        Client.getRecommendTrainList(0).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Train>>, List<Train>>() {
                    @Override
                    protected void onRespSuccessData(List<Train> data) {
                        updateTrainData(data);
                    }
                })
                .fireFree();
    }

    private void updateTrainData(List<Train> data) {
        mTrainAdapter.clear();
        for (Train train : data) {
            if (train.getTrain() != null) {
                mTrainAdapter.add(train.getTrain());
            }
        }
        mTrainAdapter.notifyDataSetChanged();
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

    private void initTrainListView() {
        mListView.setFocusable(false);
        mTrainAdapter = new TrainAdapter(getContext());
        mListView.setAdapter(mTrainAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.futureBattle, R.id.training, R.id.daily, R.id.daily1, R.id.daily2, R.id.daily3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.futureBattle:
                Launcher.with(getActivity(), BattleListActivity.class).execute();
                break;
            case R.id.training:
                Launcher.with(getActivity(), MoreTrainFeedBackActivity.class).execute();
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

    static class TrainAdapter extends ArrayAdapter<TrainProgram> {

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

            private void bindDataWithView(TrainProgram item, Context context) {
                Glide.with(context)
                        .load(item.getImageUrl())
                        .into(mTrainImg);
                mTrainType.setText(item.getTitle());
                if (item.getFinishCount() == 0) {
                    mTrainCount.setText(context.getString(R.string.have_no_train));
                } else {
                    mTrainCount.setText(context.getString(R.string.train_count, item.getFinishCount()));
                }
                mGrade.setText(context.getString(R.string.level, item.getLevel()));
                mContent.setBackground(createDrawable(new int[]{Color.parseColor("#F6D75E"), Color.parseColor("#FDB168")}, context));
            }

            private Drawable createDrawable(int[] colors, Context context) {
                GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
                gradient.setCornerRadius(Display.dp2Px(8, context.getResources()));
                return gradient;
            }
        }
    }
}
