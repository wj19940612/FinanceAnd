package com.sbai.finance.activity.leaderboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 土豪榜和学霸榜共用页面
 */

public class IngotOrSavantLeaderBoardActivity extends BaseActivity implements
        CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title)
    TitleBar mTitle;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.ingot)
    TextView mIngot;
    @BindView(R.id.rank)
    TextView mRank;
    @BindView(R.id.myBoardInfo)
    LinearLayout mMyBoardInfo;
    @BindView(R.id.tipInfo)
    TextView mTipInfo;
    private LeaderBoardAdapter mLeaderBoardAdapter;
    private Set<Integer> mSet;
    private String mType;
    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestLeaderBoardData();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingot_or_savant_board_activity);
        ButterKnife.bind(this);
        mType = getIntent().getStringExtra(Launcher.EX_PAYLOAD);
        initTitle();
        initListView();
        initLoginReceiver();
        requestLeaderBoardData();
    }

    private void initLoginReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginReceiver, intentFilter);
    }

    private void initTitle() {
        if (mType.equalsIgnoreCase(LeaderBoardRank.INGOT)) {
            mTitle.setTitle(R.string.ingot_board);
        } else {
            mTitle.setTitle(R.string.savant_board);
        }
    }

    private void initListView() {
        mSet = new HashSet<>();
        scrollToTop(mTitle, mListView);
        mLeaderBoardAdapter = new LeaderBoardAdapter(getActivity(), mType);
        mLeaderBoardAdapter.setCallback(new LeaderBoardAdapter.Callback() {
            @Override
            public void onWarshipClick(LeaderBoardRank.DataBean item) {
                if (item.getUser() != null) {
                    if (LocalUser.getUser().isLogin()) {
                        requestWorship(item.getUser().getId());
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            }
        });
        initHeaderView();
        initFooterView(60);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setLoadMoreEnable(false);
        mSwipeRefreshLayout.setAdapter(mListView, mLeaderBoardAdapter);
        mListView.setAdapter(mLeaderBoardAdapter);
        mListView.setEmptyView(mEmpty);
    }

    private void initHeaderView() {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(14, getResources()));
        view.setLayoutParams(params);
        mListView.addHeaderView(view);
    }

    private void initFooterView(int value) {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(value, getResources()));
        view.setLayoutParams(params);
        mListView.addFooterView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mLoginReceiver);
    }

    private void requestLeaderBoardData() {
        Client.getleaderBoardList(mType, null).setTag(TAG)
                .setCallback(new Callback2D<Resp<LeaderBoardRank>, LeaderBoardRank>() {
                    @Override
                    protected void onRespSuccessData(LeaderBoardRank data) {
                        updateLeaderBoardData(data);
                    }
                }).fireFree();
    }

    private void requestWorship(int id) {
        Client.worship(id, mType, null).setTag(TAG)
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

    private void updateLeaderBoardData(LeaderBoardRank data) {
        stopRefreshAnimation();
        mSet.clear();
        mLeaderBoardAdapter.clear();
        for (LeaderBoardRank.DataBean dataBean : data.getData()) {
            if (dataBean.getUser() != null && mSet.add(dataBean.getUser().getId())) {
                mLeaderBoardAdapter.add(dataBean);
            }
        }
        mLeaderBoardAdapter.notifyDataSetChanged();
        updateMyLeaderData(data);
    }

    private void updateMyLeaderData(LeaderBoardRank data) {
        if (LocalUser.getUser().isLogin()) {
            mMyBoardInfo.setVisibility(View.VISIBLE);
            mTipInfo.setVisibility(View.GONE);
            GlideApp.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(mAvatar);
            mUserName.setText(getString(R.string.me));
            if (mType.equalsIgnoreCase(LeaderBoardRank.INGOT)
                    || mType.equalsIgnoreCase(LeaderBoardRank.PROFIT)) {
                if (data.getCurr() == null) {
                    mIngot.setText(getString(R.string.ingot_number_no_blank, 0));
                    mRank.setText(getString(R.string.you_no_enter_leader_board));
                } else {
                    if (data.getCurr().getNo() < 0) {
                        mRank.setText(getString(R.string.you_no_enter_leader_board));
                    } else {
                        if (data.getCurr().getNo() > 3) {
                            mRank.setText(getString(R.string.rank, data.getCurr().getNo()));
                        }
                    }
                    mIngot.setText(getString(R.string.ingot_number_no_blank, (int) data.getCurr().getScore()));
                }
            } else if (mType.equalsIgnoreCase(LeaderBoardRank.SAVANT)) {
                if (data.getCurr() == null) {
                    mIngot.setText(getString(R.string.integrate_number_no_blank, "0"));
                    mRank.setText(getString(R.string.you_no_enter_leader_board));
                } else {
                    if (data.getCurr().getNo() < 0) {
                        mRank.setText(getString(R.string.you_no_enter_leader_board));
                    } else {
                        if (data.getCurr().getNo() > 3) {
                            mRank.setText(getString(R.string.rank, data.getCurr().getNo()));
                        }
                    }
                    mIngot.setText(getString(R.string.integrate_number_no_blank, String.valueOf((int) data.getCurr().getScore())));
                }
            }
            if (data.getCurr() != null && (data.getCurr().getNo() > 0 && data.getCurr().getNo() < 4)) {
                LeaderBoardRank.DataBean dataBean = null;
                int rank = 0;
                for (int i = 0; i < data.getData().size(); i++) {
                    if (i > 2) {
                        break;
                    }
                    if (data.getData().get(i).getUser() != null) {
                        if (data.getData().get(i).getUser().getId() == LocalUser.getUser().getUserInfo().getId()) {
                            dataBean = data.getData().get(i);
                            rank = i + 1;
                            break;
                        }
                    }
                }
                if (dataBean != null) {
                    if (mType.equalsIgnoreCase(LeaderBoardRank.INGOT)
                            || mType.equalsIgnoreCase(LeaderBoardRank.PROFIT)) {
                        if (dataBean.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(getString(R.string.ingot_number_no_blank, (int) (data.getCurr().getScore())),
                                    " +" + getString(R.string.ingot_number_no_blank, dataBean.getWorshipCount())
                                    , ContextCompat.getColor(getActivity(), R.color.unluckyText)));
                        } else {
                            mIngot.setText(getString(R.string.ingot_number_no_blank, (int) (data.getCurr().getScore())));
                        }
                    } else if (mType.equalsIgnoreCase(LeaderBoardRank.SAVANT)) {
                        if (dataBean.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(getString(R.string.integrate_number_no_blank, String.valueOf((int) data.getCurr().getScore())),
                                    " +" + getString(R.string.ingot_number_no_blank, dataBean.getWorshipCount())
                                    , ContextCompat.getColor(getActivity(), R.color.unluckyText)));
                        } else {
                            mIngot.setText(getString(R.string.integrate_number_no_blank, String.valueOf((int) (data.getCurr().getScore()))));
                        }
                    }
                }
                mRank.setText("");
                switch (rank) {
                    case 1:
                        mRank.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_rank_top_1), null);
                        break;
                    case 2:
                        mRank.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_rank_top_2), null);
                        break;
                    case 3:
                        mRank.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_rank_top_3), null);
                        break;
                }
            }
        } else {
            mMyBoardInfo.setVisibility(View.GONE);
            mTipInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadMore() {
    }


    @Override
    public void onRefresh() {
        reset();
        requestLeaderBoardData();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    private void reset() {
        mSet.clear();
    }

    @OnClick(R.id.tipInfo)
    public void onViewClicked() {
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    public static class LeaderBoardAdapter extends ArrayAdapter<LeaderBoardRank.DataBean> {
        private Callback mCallback;
        private String mType;

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public interface Callback {
            void onWarshipClick(LeaderBoardRank.DataBean item);
        }

        public LeaderBoardAdapter(@NonNull Context context, String type) {
            super(context, 0);
            mType = type;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == 1 || position == 2) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            switch (getItemViewType(position)) {
                case 0:
                    TopThreeViewHolder viewHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_leader_board_top_three, null, true);
                        viewHolder = new TopThreeViewHolder(convertView);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (TopThreeViewHolder) convertView.getTag();
                    }
                    viewHolder.bindDataWithView(getItem(position), position, getContext(), mCallback, mType);
                    break;
                case 1:
                    NormalViewHolder normalViewHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_leader_board_normal, null, true);
                        normalViewHolder = new NormalViewHolder(convertView);
                        convertView.setTag(normalViewHolder);
                    } else {
                        normalViewHolder = (NormalViewHolder) convertView.getTag();
                    }
                    normalViewHolder.bindDataWithView(getItem(position), position, getContext(), mType);
                    break;
                default:
            }
            return convertView;
        }

        static class TopThreeViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.ingot)
            TextView mIngot;
            @BindView(R.id.rankImage)
            ImageView mRankImage;
            @BindView(R.id.worship)
            TextView mWorship;
            @BindView(R.id.rank)
            TextView mRank;

            TopThreeViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final LeaderBoardRank.DataBean item, int position, Context context, final Callback callback, String type) {
                mWorship.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onWarshipClick(item);
                            if (LocalUser.getUser().isLogin()) {
                                mWorship.setEnabled(false);
                            }
                        }
                    }
                });
                if (item.isWorship()) {
                    mWorship.setEnabled(true);
                } else {
                    mWorship.setEnabled(false);
                }
                GlideApp.with(context)
                        .load(item.getUser().getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);
                mUserName.setText(item.getUser().getUserName());

                switch (position) {
                    case 0:
                        mRankImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_rank_top_1));
                        break;
                    case 1:
                        mRankImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_rank_top_2));
                        break;
                    case 2:
                        mRankImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_rank_top_3));
                        break;
                }
                switch (type) {
                    case LeaderBoardRank.INGOT:
                    case LeaderBoardRank.PROFIT:
                        if (item.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(context.getString(R.string.ingot_number_no_blank, (int) item.getScore()),
                                    " +" + context.getString(R.string.ingot_number_no_blank, item.getWorshipCount())
                                    , ContextCompat.getColor(context, R.color.unluckyText)));
                        } else {
                            mIngot.setText(context.getString(R.string.ingot_number_no_blank, (int) item.getScore()));
                        }
                        break;
                    case LeaderBoardRank.SAVANT:
                        if (item.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(context.getString(R.string.integrate_number_no_blank, String.valueOf((int) item.getScore())),
                                    " +" + context.getString(R.string.ingot_number_no_blank, (int) item.getWorshipCount())
                                    , ContextCompat.getColor(context, R.color.unluckyText)));
                        } else {
                            mIngot.setText(context.getString(R.string.integrate_number_no_blank, String.valueOf((int) item.getScore())));
                        }
                        break;
                }
            }

        }

        static class NormalViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.ingot)
            TextView mIngot;
            @BindView(R.id.rank)
            TextView mRank;

            NormalViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(LeaderBoardRank.DataBean item, int position, Context context, String type) {
                GlideApp.with(context)
                        .load(item.getUser().getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);
                mUserName.setText(item.getUser().getUserName());
                switch (type) {
                    case LeaderBoardRank.INGOT:
                    case LeaderBoardRank.PROFIT:
                        mIngot.setText(context.getString(R.string.ingot_number_no_blank, (int) item.getScore()));
                        break;
                    case LeaderBoardRank.SAVANT:
                        mIngot.setText(context.getString(R.string.integrate_number_no_blank, String.valueOf((int) item.getScore())));
                        break;
                }
                mRank.setText(String.valueOf(position + 1));
            }
        }
    }
}
