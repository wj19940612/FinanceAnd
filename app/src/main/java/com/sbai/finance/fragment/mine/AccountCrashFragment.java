package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class AccountCrashFragment extends BaseFragment {

    @BindView(R.id.fund)
    TextView mFund;
    @BindView(R.id.recharge)
    TextView mRecharge;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.adsorb_text)
    TextView mAdsorbText;
    @BindView(R.id.dataLayout)
    FrameLayout mDataLayout;

    private Unbinder mBind;
    private FundDetailAdapter mFundDetailAdapter;
    private int mPageNo = 0;
    private ArrayList<AccountFundDetail> mFundDetailList;
    private boolean mLoadMore;

    public AccountCrashFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_fund, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFundDetailList = new ArrayList<>();
        initRecycleView();
        updateUserFund(0);
        requestDetailList(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageNo = 0;
                requestDetailList(true);
            }
        });

    }

    private void updateUserFund(double fund) {
        SpannableString spannableString = StrUtil.mergeTextWithRatio(getString(R.string.mine_crash), "\n" + FinanceUtil.formatWithThousandsSeparator(fund), 2.7f);
        mFund.setText(spannableString);
    }

    public void setUserFundData(double fund) {
        updateUserFund(fund);
    }

    private void initRecycleView() {
        mFundDetailAdapter = new FundDetailAdapter(mFundDetailList, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFundDetailAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }

    private void handleRecycleViewScroll(RecyclerView recyclerView) {
        if (isSlideToBottom(mRecyclerView) && mLoadMore) {
            requestDetailList(false);
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

            if (transViewStatus == FundDetailAdapter.HAS_STICKY_VIEW) {
                if (transInfoView.getTop() > 0) {
                    mAdsorbText.setTranslationY(dealtY);
                } else {
                    mAdsorbText.setTranslationY(0);
                }
            } else if (transViewStatus == FundDetailAdapter.NONE_STICKY_VIEW) {
                mAdsorbText.setTranslationY(0);
            }
        }
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }


    private void requestDetailList(final boolean isRefresh) {
        Client.getCrashDetail(mPageNo, Client.DEFAULT_PAGE_SIZE)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<AccountFundDetail>>, List<AccountFundDetail>>() {
                    @Override
                    protected void onRespSuccessData(List<AccountFundDetail> data) {
                        updateDetailList(data, isRefresh);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateDetailList(List<AccountFundDetail> fundDetailList, boolean isRefresh) {
        if (fundDetailList == null || fundDetailList.isEmpty() && mFundDetailList.isEmpty()) {
            mDataLayout.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mDataLayout.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (isRefresh) {
                mFundDetailAdapter.clear();
            }
            if (fundDetailList.size() < Client.DEFAULT_PAGE_SIZE) {
                mLoadMore = false;
            } else {
                mLoadMore = true;
                mPageNo++;
            }
            mFundDetailAdapter.addAll(fundDetailList);
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class FundDetailAdapter extends RecyclerView.Adapter<FundDetailAdapter.ViewHolder> {

        public static final int HAS_STICKY_VIEW = 2;
        public static final int NONE_STICKY_VIEW = 3;
        private ArrayList<AccountFundDetail> mFundDetailArrayList;
        private Context mContext;

        public FundDetailAdapter(ArrayList<AccountFundDetail> fundDetailArrayList, Context context) {
            this.mFundDetailArrayList = fundDetailArrayList;
            this.mContext = context;
        }

        public void addAll(List<AccountFundDetail> fundDetailArrayList) {
            mFundDetailArrayList.addAll(fundDetailArrayList);
            this.notifyItemRangeChanged(0, mFundDetailArrayList.size());
        }

        public void clear() {
            mFundDetailArrayList.clear();
            notifyItemRangeRemoved(0, mFundDetailArrayList.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_fund_detail, null);
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
            holder.itemView.setContentDescription(DateUtil.formatMonth(mFundDetailArrayList.get(position).getCreateTime()));
        }

        @Override
        public int getItemCount() {
            return mFundDetailArrayList != null ? mFundDetailArrayList.size() : 0;
        }

        private boolean isTheDifferentMonth(int position) {
            if (position == 0) {
                return true;
            }
            AccountFundDetail pre = mFundDetailArrayList.get(position - 1);
            AccountFundDetail next = mFundDetailArrayList.get(position);
            //判断两个时间在不在一个月内  不是就要显示标题
            long preTime = pre.getCreateTime();
            long nextTime = next.getCreateTime();
            return !DateUtil.isInThisMonth(nextTime, preTime);
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

            public void bindDataWithView(AccountFundDetail fundDetail, int position, boolean theDifferentMonth, Context context) {
                if (theDifferentMonth) {
                    mAdsorbText.setVisibility(View.VISIBLE);
                    mAdsorbText.setText(DateUtil.formatMonth(fundDetail.getCreateTime()));
                } else {
                    mAdsorbText.setVisibility(View.GONE);
                }
                mTime.setText(DateUtil.formatUserFundDetailTime(fundDetail.getCreateTime()));

                if (!TextUtils.isEmpty(fundDetail.getPlatformName())) {
                    mPayWay.setText(context.getString(R.string.money_from, fundDetail.getRemark(), fundDetail.getPlatformName()));
                } else {
                    mPayWay.setText(fundDetail.getRemark());
                }
                if (fundDetail.getType() < 0) {
                    mMoney.setSelected(false);
                    mMoney.setText(context.getString(R.string.minus_string, FinanceUtil.formatWithScale(fundDetail.getMoney())));
                } else {
                    mMoney.setSelected(true);
                    mMoney.setText(context.getString(R.string.plus_string, FinanceUtil.formatWithScale(fundDetail.getMoney())));
                }
            }
        }
    }
}
