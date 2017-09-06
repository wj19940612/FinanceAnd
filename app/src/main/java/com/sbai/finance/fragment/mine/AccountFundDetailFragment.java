package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.wallet.RechargeActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.autofit.AutofitTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/21.
 * 积分 现金 元宝流水明细页面
 */

public class AccountFundDetailFragment extends BaseFragment {

    private static final String KEY_TYPE = "TYPE";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.adsorb_text)
    TextView mAdsorbText;
    @BindView(R.id.dataLayout)
    FrameLayout mDataLayout;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recharge)
    TextView mRecharge;
    @BindView(R.id.fundName)
    TextView mFundName;
    @BindView(R.id.fundNumber)
    AutofitTextView mFundNumber;

    private Unbinder mBind;

    //元宝 或积分
    private int mFundType;
    private int mPage = 0;
    private ArrayList<AccountFundDetail> mAccountFundDetailList;
    private UserFundDetailAdapter mUserFundDetailAdapter;
    private boolean mLoadMore = true;


    public static AccountFundDetailFragment newInstance(int type) {
        Bundle args = new Bundle();
        AccountFundDetailFragment fragment = new AccountFundDetailFragment();
        args.putInt(KEY_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFundType = getArguments().getInt(KEY_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_fund_detail, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccountFundDetailList = new ArrayList<>();
        initRecycleView();
        requestDetailList(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                requestDetailList(true);
            }
        });
        updateUserFund(0.0);
    }

    public void updateUserFund(double fund) {
        switch (mFundType) {
            case AccountFundDetail.TYPE_CRASH:
                mFundName.setText(R.string.mine_crash);
                mFundNumber.setText(FinanceUtil.formatWithThousandsSeparatorAndScale(fund, 0));
                break;
            case AccountFundDetail.TYPE_INGOT:
                mFundName.setText(R.string.mine_ingot);
                mFundNumber.setText(FinanceUtil.formatWithThousandsSeparator(fund));
                break;
            case AccountFundDetail.TYPE_SCORE:
                mFundName.setText(R.string.mine_score);
                mFundNumber.setText(FinanceUtil.formatWithThousandsSeparator(fund));
                break;
        }

    }

    private void initRecycleView() {
        mUserFundDetailAdapter = new UserFundDetailAdapter(mAccountFundDetailList, getActivity());
        mUserFundDetailAdapter.setDetailType(mFundType);
        mRecyclerView.setAdapter(mUserFundDetailAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                handleRecycleScroll(recyclerView);
            }
        });
    }

    //RecycleView 滑动的时候 根据tag 显示对应的吸顶文字
    private void handleRecycleScroll(RecyclerView recyclerView) {
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

            if (transViewStatus == UserFundDetailAdapter.HAS_STICKY_VIEW) {
                if (transInfoView.getTop() > 0) {
                    mAdsorbText.setTranslationY(dealtY);
                } else {
                    mAdsorbText.setTranslationY(0);
                }
            } else if (transViewStatus == UserFundDetailAdapter.NONE_STICKY_VIEW) {
                mAdsorbText.setTranslationY(0);
            }
        }
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void requestDetailList(final boolean isRefresh) {
        switch (mFundType) {
            case AccountFundDetail.TYPE_CRASH:
                requestUserCrashDetail(isRefresh);
                break;
            default:
                requestUserIngotOrScoreDetail(isRefresh);
                break;
        }

    }

    private void requestUserCrashDetail(final boolean isRefresh) {
        Client.requestUserFundCrashDetail(mPage)
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

    private void requestUserIngotOrScoreDetail(final boolean isRefresh) {
        Client.requestUserIngotOrScoreDetailList(mFundType, mPage)
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

    private void updateDetailList(List<AccountFundDetail> exchangeDetailList, boolean isRefresh) {
        if (exchangeDetailList == null || exchangeDetailList.isEmpty() && mAccountFundDetailList.isEmpty()) {
            mDataLayout.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
            stopRefreshAnimation();
        } else {
            mDataLayout.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (isRefresh) {
                mUserFundDetailAdapter.clear();
            }

            if (exchangeDetailList.size() < Client.DEFAULT_PAGE_SIZE) {
                mLoadMore = false;
            } else {
                mLoadMore = true;
                mPage++;
            }
            mUserFundDetailAdapter.addAll(exchangeDetailList);
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

    @OnClick(R.id.recharge)
    public void onViewClicked() {
        switch (mFundType) {
            case AccountFundDetail.TYPE_CRASH:
                Launcher.with(getActivity(), RechargeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE,AccountFundDetail.TYPE_CRASH)
                        .execute();
                break;
            case AccountFundDetail.TYPE_INGOT:
                Launcher.with(getActivity(), RechargeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE,AccountFundDetail.TYPE_INGOT)
                        .execute();
                break;
            case AccountFundDetail.TYPE_SCORE:
                Launcher.with(getActivity(), RechargeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE,AccountFundDetail.TYPE_SCORE)
                        .execute();
                break;
        }
    }

    class UserFundDetailAdapter extends RecyclerView.Adapter<UserFundDetailAdapter.ViewHolder> {

        public static final int HAS_STICKY_VIEW = 2;
        public static final int NONE_STICKY_VIEW = 3;
        private ArrayList<AccountFundDetail> mExchangeDetailArrayList;
        private Context mContext;
        private int mFundType;

        public UserFundDetailAdapter(ArrayList<AccountFundDetail> detailArrayList, Context context) {
            this.mExchangeDetailArrayList = detailArrayList;
            this.mContext = context;
        }

        public void addAll(List<AccountFundDetail> detailArrayList) {
            mExchangeDetailArrayList.addAll(detailArrayList);
            this.notifyItemRangeChanged(0, mExchangeDetailArrayList.size());
        }

        public void clear() {
            mExchangeDetailArrayList.clear();
            notifyItemRangeRemoved(0, mExchangeDetailArrayList.size());
        }

        public void setDetailType(int fundType) {
            mFundType = fundType;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_fund_detail, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mExchangeDetailArrayList.get(position), position, isTheDifferentMonth((position)), mContext, mFundType);
            if (isTheDifferentMonth(position)) {
                holder.itemView.setTag(HAS_STICKY_VIEW);
            } else {
                holder.itemView.setTag(NONE_STICKY_VIEW);
            }
            holder.itemView.setContentDescription(DateUtil.formatMonth(mExchangeDetailArrayList.get(position).getCreateTime()));
        }

        @Override
        public int getItemCount() {
            return mExchangeDetailArrayList != null ? mExchangeDetailArrayList.size() : 0;
        }

        private boolean isTheDifferentMonth(int position) {
            if (position == 0) {
                return true;
            }
            AccountFundDetail pre = mExchangeDetailArrayList.get(position - 1);
            AccountFundDetail next = mExchangeDetailArrayList.get(position);
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

            public void bindDataWithView(AccountFundDetail detail, int position,
                                         boolean theDifferentMonth, Context context, int fundType) {
                if (theDifferentMonth) {
                    mAdsorbText.setVisibility(View.VISIBLE);
                    mAdsorbText.setText(DateUtil.formatMonth(detail.getCreateTime()));
                } else {
                    mAdsorbText.setVisibility(View.GONE);
                }
                mTime.setText(DateUtil.formatUserFundDetailTime(detail.getCreateTime()));
                mPayWay.setText(detail.getRemark());
                if (detail.isIngot()) {
                    if (detail.getFlowType() < 0) {
                        mMoney.setText(context.getString(R.string.plus_int, (int) detail.getMoney()));
                        mMoney.setSelected(false);
                    } else {
                        mMoney.setText(context.getString(R.string.minus_int, (int) detail.getMoney()));
                        mMoney.setSelected(true);
                    }
                } else {
                    if (detail.getFlowType() < 0) {
                        mMoney.setSelected(false);
                        mMoney.setText(context.getString(R.string.plus_string, FinanceUtil.formatWithScale(detail.getMoney())));
                    } else {
                        mMoney.setText(context.getString(R.string.minus_string, FinanceUtil.formatWithScale(detail.getMoney())));
                        mMoney.setSelected(true);
                    }
                }
            }
        }
    }
}
