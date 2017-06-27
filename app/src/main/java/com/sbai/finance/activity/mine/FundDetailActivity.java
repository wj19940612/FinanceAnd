package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.FundDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
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
public class FundDetailActivity extends BaseActivity {
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

    private int mPageSize = 20;
    private int mPageNo = 0;
    private ArrayList<FundDetail> mFundDetailArrayList;
    private TheDetailAdapter mTheDetailAdapter;
    private boolean mLoadMore = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mFundDetailArrayList = new ArrayList<>();
        mTheDetailAdapter = new TheDetailAdapter(mFundDetailArrayList, this);

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
                handleRecycleViewScroll(recyclerView);
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

    private void handleRecycleViewScroll(RecyclerView recyclerView) {
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
                .setCallback(new Callback2D<Resp<List<FundDetail>>, List<FundDetail>>() {
                    @Override
                    protected void onRespSuccessData(List<FundDetail> data) {
                        updateDetailList(data);
                    }
                })
                .fire();
    }

    private void updateDetailList(List<FundDetail> fundDetailList) {
        if (fundDetailList == null || fundDetailList.isEmpty() && mFundDetailArrayList.isEmpty()) {
            mDataLayout.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            mDataLayout.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mTheDetailAdapter.clear();
                mSwipeRefreshLayout.setRefreshing(false);
            }
            mFundDetailArrayList.addAll(fundDetailList);
            if (fundDetailList.size() < mPageSize) {
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
        private ArrayList<FundDetail> mFundDetailArrayList;
        private Context mContext;

        public TheDetailAdapter(ArrayList<FundDetail> fundDetailArrayList, Context context) {
            this.mFundDetailArrayList = fundDetailArrayList;
            this.mContext = context;
        }

        public void addAll(ArrayList<FundDetail> fundDetailArrayList) {
            this.addAll(mFundDetailArrayList);
            notifyItemRangeInserted(mFundDetailArrayList.size() - fundDetailArrayList.size(), mFundDetailArrayList.size());
        }

        public void clear() {
            mFundDetailArrayList.clear();
            notifyItemRangeRemoved(0, mFundDetailArrayList.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mFundDetailArrayList.get(position), position, isTheDifferentMonth((position)), mContext);
            if (isTheDifferentMonth(position)) {
                holder.itemView.setTag(HAS_STICKY_VIEW);
            } else {
                holder.itemView.setTag(NONE_STICKY_VIEW);
            }
            holder.itemView.setContentDescription(DateUtil.getFormatMonth(mFundDetailArrayList.get(position).getCreateTime()));
        }

        @Override
        public int getItemCount() {
            return mFundDetailArrayList != null ? mFundDetailArrayList.size() : 0;
        }

        private boolean isTheDifferentMonth(int position) {
            if (position == 0) {
                return true;
            }
            FundDetail pre = mFundDetailArrayList.get(position - 1);
            FundDetail next = mFundDetailArrayList.get(position);
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

            public void bindDataWithView(FundDetail fundDetail, int position, boolean theDifferentMonth, Context context) {
                if (theDifferentMonth) {
                    mAdsorbText.setVisibility(View.VISIBLE);
                    mAdsorbText.setText(DateUtil.getFormatMonth(fundDetail.getCreateTime()));
                } else {
                    mAdsorbText.setVisibility(View.GONE);
                }
                mTime.setText(StrUtil.mergeTextWithRatio(DateUtil.getDetailFormatTime(fundDetail.getCreateTime()), "\n" + DateUtil.format(fundDetail.getCreateTime(), DateUtil.FORMAT_HOUR_MINUTE), 0.9f));

                if (!TextUtils.isEmpty(fundDetail.getPlatformName())) {
                    mPayWay.setText(context.getString(R.string.money_from, fundDetail.getRemark(), fundDetail.getPlatformName()));
                } else {
                    mPayWay.setText(fundDetail.getRemark());
                }
                if (fundDetail.getType() < 0) {
                    mMoney.setText(context.getString(R.string.minus_yuan, FinanceUtil.formatWithScale(fundDetail.getMoney())));
                } else {
                    mMoney.setText(context.getString(R.string.earnings_yuan, FinanceUtil.formatWithScale(fundDetail.getMoney())));
                }
            }
        }
    }
}
