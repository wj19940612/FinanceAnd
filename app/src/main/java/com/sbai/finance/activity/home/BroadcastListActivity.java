package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.model.Broadcast;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 广播
 */

public class BroadcastListActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private BroadcastAdapter mBroadcastAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        ButterKnife.bind(this);
        initSwipeRefreshView();
        initRecyclerView();
        requestBroadcast();
    }

    private void initSwipeRefreshView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBroadcast();
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setEmptyView(mEmpty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mBroadcastAdapter = new BroadcastAdapter(getActivity()));
    }

    private void requestBroadcast() {
        Client.getBroadcast().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Broadcast>>, List<Broadcast>>() {
                    @Override
                    protected void onRespSuccessData(List<Broadcast> data) {
                        mBroadcastAdapter.clear();
                        mBroadcastAdapter.addAll(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    class BroadcastAdapter extends RecyclerView.Adapter<BroadcastAdapter.ViewHolder> {
        private List<Broadcast> mBroadcastList;
        private Context mContext;

        BroadcastAdapter(Context context) {
            mContext = context;
            mBroadcastList = new ArrayList<>();
        }

        public void addAll(List<Broadcast> broadcasts) {
            mBroadcastList.addAll(broadcasts);
            notifyDataSetChanged();
        }

        public void clear() {
            mBroadcastList.clear();
            notifyDataSetChanged();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_broadcast, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mBroadcastList.get(position), mContext);
        }

        @Override
        public int getItemCount() {
            return mBroadcastList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.imgMore)
            ImageView mImgMore;
            @BindView(R.id.contentArea)
            LinearLayout mContentArea;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(final Broadcast broadcast, Context context) {
                mContent.setText(broadcast.getContent());
                mTime.setText(DateUtil.format(broadcast.getCreateTime(), DateUtil.FORMAT_SPECIAL_SLASH));
                if (TextUtils.isEmpty(broadcast.getLink())) {
                    mImgMore.setVisibility(View.INVISIBLE);
                } else {
                    mImgMore.setVisibility(View.VISIBLE);
                    mContentArea.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (broadcast.isH5Style()) {
                                Launcher.with(getActivity(), WebActivity.class)
                                        .putExtra(WebActivity.EX_URL, broadcast.getLink())
                                        .execute();
                            } else {
                                Launcher.with(getActivity(), WebActivity.class)
                                        .putExtra(WebActivity.EX_TITLE, broadcast.getTitle())
                                        .putExtra(WebActivity.EX_HTML, broadcast.getContent())
                                        .execute();
                            }
                        }
                    });
                }
            }
        }
    }
}
