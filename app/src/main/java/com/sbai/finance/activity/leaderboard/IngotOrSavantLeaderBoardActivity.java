package com.sbai.finance.activity.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 土豪榜和学霸榜共用页面
 */

public class IngotOrSavantLeaderBoardActivity extends BaseActivity implements
        CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int TYPE_INGOT = 0;
    public static final int TYPE_SAVANT = 1;

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
    private LeaderBoardAdapter mLeaderBoardAdapter;
    private Set<Integer> mSet;
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingot_or_savant_board_activity);
        ButterKnife.bind(this);
        mType = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        initTitle();
        initListView();
        requestLeaderBoardData();
        requestMyLeaderData();
    }

    private void initTitle() {
        if (mType == TYPE_INGOT) {
            mTitle.setTitle(R.string.ingot_board);
        } else {
            mTitle.setTitle(R.string.savant_board);
        }
    }

    private void initListView() {
        mSet = new HashSet<>();
        mLeaderBoardAdapter = new LeaderBoardAdapter(getActivity());
        mListView.setAdapter(mLeaderBoardAdapter);
        mListView.setEmptyView(mEmpty);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startScheduleJob(10 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRefreshAnimation();
    }

    private void requestLeaderBoardData() {

    }

    private void requestMyLeaderData() {

    }

    private void updateLeaderBoardData() {

    }

    private void updateeMyLeaderData() {

    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestLeaderBoardData();
    }

    @Override
    public void onLoadMore() {
        requestLeaderBoardData();
    }


    @Override
    public void onRefresh() {
        reset();
        requestLeaderBoardData();
        requestMyLeaderData();
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
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    public static class LeaderBoardAdapter extends ArrayAdapter<LeaderBoardRank> {
        private Callback mCallback;

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public interface Callback {
            void onWarshipClick(LeaderBoardRank item);
        }

        public LeaderBoardAdapter(@NonNull Context context) {
            super(context, 0);
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
                    viewHolder.bindDataWithView(getItem(position), position, getContext(), mCallback);
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
                    normalViewHolder.bindDataWithView(getItem(position), getContext());
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

            public void bindDataWithView(final LeaderBoardRank item, int position, Context context, final Callback callback) {
                mWorship.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onWarshipClick(item);
                            mWorship.setEnabled(false);
                        }
                    }
                });
                Glide.with(context)
                        .load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);
                mUserName.setText(item.getUserName());
                mIngot.setText(context.getString(R.string.ingot_number, String.valueOf(item.getIngot())));
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

            public void bindDataWithView(LeaderBoardRank item, Context context) {
                Glide.with(context)
                        .load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);
                mUserName.setText(item.getUserName());
                mIngot.setText(context.getString(R.string.ingot_number, String.valueOf(item.getIngot())));
                mRank.setText(item.getRank());

            }
        }
    }
}
