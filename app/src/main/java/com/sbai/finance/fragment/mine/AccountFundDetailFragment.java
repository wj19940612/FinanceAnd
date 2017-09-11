package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.content.Intent;
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
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.fund.RechargeActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.UmengCountEventId;
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

    private static final int REQ_CODE_RECHARGE_CRASH = 4920;
    private static final int REQ_CODE_RECHARGE_INGOOT = 45550;
    private static final int REQ_CODE_RECHARGE_SCORE = 88820;

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
    //用户账户资金 元宝
    private double mFundCount;
    private UserFundInfo mUserFundInfo;


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
        updateUserFund(null);
    }

    public void updateUserFund(UserFundInfo fund) {
        mUserFundInfo = fund;
        switch (mFundType) {
            case AccountFundDetail.TYPE_CRASH:
                mFundName.setText(R.string.mine_crash);
                mFundCount = fund != null ? fund.getMoney() : 0;
                mFundNumber.setText(FinanceUtil.formatWithThousandsSeparator(mFundCount));
                break;
            case AccountFundDetail.TYPE_INGOT:
                mFundName.setText(R.string.mine_ingot);
                mFundCount = fund != null ? fund.getYuanbao() : 0;
                mFundNumber.setText(FinanceUtil.formatWithThousandsSeparatorAndScale(mFundCount, 0));
                break;
            case AccountFundDetail.TYPE_SCORE:
                mFundName.setText(R.string.mine_score);
                mFundCount = fund != null ? fund.getCredit() : 0;
                mFundNumber.setText(FinanceUtil.formatWithThousandsSeparator(mFundCount));
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


    private void requestDetailList(final boolean isRefresh) {
        Client.requestAccountFundDetailList(mFundType, mPage)
                .setIndeterminate(this)
                .setTag(TAG)
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
                .fireFree();

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
                mPage++;
                mLoadMore = true;
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
                umengEventCount(UmengCountEventId.WALLET_RECHARGE);
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                intent.putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_CRASH);
                startActivityForResult(intent, REQ_CODE_RECHARGE_CRASH);
                break;
            case AccountFundDetail.TYPE_INGOT:
                Intent ingotIntent = new Intent(getActivity(), VirtualProductExchangeActivity.class);
                ingotIntent.putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT);
                ingotIntent.putExtra(ExtraKeys.USER_FUND, mUserFundInfo != null ? mUserFundInfo.getMoney() : 0);
                startActivityForResult(ingotIntent, REQ_CODE_RECHARGE_CRASH);
                break;
            case AccountFundDetail.TYPE_SCORE:
                Intent scoreIntent = new Intent(getActivity(), VirtualProductExchangeActivity.class);
                scoreIntent.putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_SCORE);
                scoreIntent.putExtra(ExtraKeys.USER_FUND, mUserFundInfo != null ? Double.parseDouble(mUserFundInfo.getYuanbao() + "") : 0);
                startActivityForResult(scoreIntent, REQ_CODE_RECHARGE_CRASH);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == BaseActivity.RESULT_OK) {
            mPage = 0;
            requestDetailList(true);
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
//            this.notifyItemRangeChanged(0, mExchangeDetailArrayList.size());
            notifyDataSetChanged();
        }

        public void clear() {
            mExchangeDetailArrayList.clear();
            notifyDataSetChanged();
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

                mPayWay.setText(detail.getRemark());
//                mPayWay.setText(context.getString(R.string.money_from, detail.getRemark(), detail.getPlatformName()));
                if (fundType == AccountFundDetail.TYPE_CRASH) {

                    if (detail.getType() > 0) {
                        mMoney.setSelected(false);
                        mMoney.setText(context.getString(R.string.plus_string, FinanceUtil.formatWithScale(detail.getMoney())));
                    } else {
                        mMoney.setSelected(true);
                        mMoney.setText(context.getString(R.string.minus_string, FinanceUtil.formatWithScale(detail.getMoney())));
                    }
                } else {

                    if (detail.isIngot()) {
                        if (detail.getType() > 0) {
                            mMoney.setText(context.getString(R.string.plus_int, (int) detail.getMoney()));
                            mMoney.setSelected(false);
                        } else {
                            mMoney.setText(context.getString(R.string.minus_int, (int) detail.getMoney()));
                            mMoney.setSelected(true);
                        }
                    } else {
                        if (detail.getType() > 0) {
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
}
