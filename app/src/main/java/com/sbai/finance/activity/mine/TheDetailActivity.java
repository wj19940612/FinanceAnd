package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Detail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ${wangJie} on 2017/6/21.
 * 明细界面
 */
public class TheDetailActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.adsorb_text)
    TextView mAdsorbText;
    @BindView(R.id.dataLayout)
    FrameLayout mDataLayout;

    private int mPageSize = 10;
    private int mPageNo = 0;
    private ArrayList<Detail> mDetailArrayList;
    private TheDetailAdapter mTheDetailAdapter;
    private boolean mLoadMore = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mDetailArrayList = new ArrayList<>();
        mTheDetailAdapter = new TheDetailAdapter(mDetailArrayList, this);

        mRecyclerView.setAdapter(mTheDetailAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(mRecyclerView) && mLoadMore) {
                    requestDetailList();
                }
                View stickyInfoView = recyclerView.findChildViewUnder(
                        mAdsorbText.getMeasuredWidth() / 2, 5);

                if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                    mAdsorbText.setText(String.valueOf(stickyInfoView.getContentDescription()));
                }

                View transInfoView = recyclerView.findChildViewUnder(
                        mAdsorbText.getMeasuredWidth() / 2, mAdsorbText.getMeasuredHeight() + 1);
                if (transInfoView != null && transInfoView.getTag() != null) {
                    int transViewStatus = (int) transInfoView.getTag();
                    int dealtY = transInfoView.getTop() - mAdsorbText.getMeasuredHeight();

                    if (transViewStatus == TheDetailAdapter.HAS_STICKY_VIEW) {
                        if (transInfoView.getTop() > 0) {
                            mAdsorbText.setTranslationY(dealtY);
                        } else {
                            mAdsorbText.setTranslationY(0);
                        }
                    } else if (transViewStatus == TheDetailAdapter.NONE_STICKY_VIEW) {
                        mAdsorbText.setTranslationY(0);
                    }
                }
            }
        });
        requestDetailList();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageNo = 0;
                requestDetailList();
            }
        });
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }


    private void requestDetailList() {
        Client.getDetail(mPageNo, mPageSize)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Detail>>, List<Detail>>() {
                    @Override
                    protected void onRespSuccessData(List<Detail> data) {
                        updateDetailList(data);
                    }
                })
                .fire();
    }

    private void updateDetailList(List<Detail> detailList) {
        if (detailList == null || detailList.isEmpty() && mDetailArrayList.isEmpty()) {
            mDataLayout.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mDataLayout.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mTheDetailAdapter.clear();
                mSwipeRefreshLayout.setRefreshing(false);
            }
            mDetailArrayList.addAll(detailList);
            if (detailList.size() < mPageSize) {
                mLoadMore = false;
            } else {
                mLoadMore = true;
                mPageNo++;
            }
            mTheDetailAdapter.notifyDataSetChanged();
        }
    }

    class TheDetailAdapter extends RecyclerView.Adapter<TheDetailAdapter.ViewHolder> {

        public static final int HAS_STICKY_VIEW = 2;
        public static final int NONE_STICKY_VIEW = 3;
        private ArrayList<Detail> mDetailArrayList;
        private Context mContext;

        public TheDetailAdapter(ArrayList<Detail> detailArrayList, Context context) {
            this.mDetailArrayList = detailArrayList;
            this.mContext = context;
        }

        public void addAll(ArrayList<Detail> detailArrayList) {
            this.addAll(mDetailArrayList);
            notifyItemRangeInserted(mDetailArrayList.size() - detailArrayList.size(), mDetailArrayList.size());
        }

        public void clear() {
            mDetailArrayList.clear();
            notifyItemRangeRemoved(0, mDetailArrayList.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mDetailArrayList.get(position), position, isTheDifferentMonth((position)), mContext);
            if (isTheDifferentMonth(position)) {
                holder.itemView.setTag(HAS_STICKY_VIEW);
            } else {
                holder.itemView.setTag(NONE_STICKY_VIEW);
            }
            holder.itemView.setContentDescription(DateUtil.getFormatMonth(mDetailArrayList.get(position).getCreateTime()));
        }

        @Override
        public int getItemCount() {
            return mDetailArrayList != null ? mDetailArrayList.size() : 0;
        }

        private boolean isTheDifferentMonth(int position) {
            if (position == 0) {
                return true;
            }
            Detail pre = mDetailArrayList.get(position - 1);
            Detail next = mDetailArrayList.get(position);
            //判断两个时间在不在一个月内  不是就要显示标题
            long preTime = pre.getCreateTime();
            long nextTime = next.getCreateTime();
            if (DateUtil.isInThisMonth(nextTime, preTime)) {
                return false;
            }
            return true;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.adsorb_text)
            TextView mAdsorbText;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.payWay)
            TextView mPayWay;
            @BindView(R.id.money)
            TextView mMoney;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(Detail detail, int position, boolean theDifferentMonth, Context context) {
                if (theDifferentMonth) {
                    mAdsorbText.setVisibility(View.VISIBLE);
                    mAdsorbText.setText(DateUtil.getFormatMonth(detail.getCreateTime()));
                } else {
                    mAdsorbText.setVisibility(View.GONE);
                }
                mTime.setText(StrUtil.mergeTextWithRatio(DateUtil.getFeedbackFormatTime(detail.getCreateTime()), "\n" + DateUtil.format(detail.getCreateTime(), DateUtil.FORMAT_HOUR_MINUTE), 0.9f));
                mPayWay.setText(detail.getRemark());
                mMoney.setText(context.getString(R.string.RMB, String.valueOf(detail.getMoney())));
            }
        }
    }
}
