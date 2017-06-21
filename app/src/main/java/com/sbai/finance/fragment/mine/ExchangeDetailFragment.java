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

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.cornucopia.ExchangeDetailModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/21.
 */

public class ExchangeDetailFragment extends BaseFragment {

    private static final String KEY_TYPE = "TYPE";
    private static final String KEY_DIRECTION = "direction";
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

    private Unbinder mBind;

    //元宝 或积分
    private int mType;
    //收入 支出
    private int mDirection;

    private int mPage = 0;
    private ArrayList<ExchangeDetailModel> mExchangeDetailModelList;
    private ExchangeDetailAdapter mExchangeDetailAdapter;
    private boolean mLoadMore = true;


    public static ExchangeDetailFragment newInstance(int type, int direction) {
        Bundle args = new Bundle();
        ExchangeDetailFragment fragment = new ExchangeDetailFragment();
        args.putInt(KEY_TYPE, type);
        args.putInt(KEY_DIRECTION, direction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(KEY_TYPE);
            mDirection = getArguments().getInt(KEY_DIRECTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detail, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mExchangeDetailModelList = new ArrayList<>();
        mExchangeDetailAdapter = new ExchangeDetailAdapter(mExchangeDetailModelList, getActivity());
        mRecyclerView.setAdapter(mExchangeDetailAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    if (transViewStatus == ExchangeDetailAdapter.HAS_STICKY_VIEW) {
                        if (transInfoView.getTop() > 0) {
                            mAdsorbText.setTranslationY(dealtY);
                        } else {
                            mAdsorbText.setTranslationY(0);
                        }
                    } else if (transViewStatus == ExchangeDetailAdapter.NONE_STICKY_VIEW) {
                        mAdsorbText.setTranslationY(0);
                    }
                }
            }
        });
        requestDetailList();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                requestDetailList();
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
        Client.getExchangeDetailList(mDirection, mType, mPage)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<ExchangeDetailModel>>, List<ExchangeDetailModel>>() {
                    @Override
                    protected void onRespSuccessData(List<ExchangeDetailModel> data) {
                        updateExchangeDetailList(data);
                    }
                })
                .fire();
    }

    private void updateExchangeDetailList(List<ExchangeDetailModel> exchangeDetailList) {
        if (exchangeDetailList == null || exchangeDetailList.isEmpty() && mExchangeDetailModelList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mExchangeDetailAdapter.clear();
                mSwipeRefreshLayout.setRefreshing(false);
            }
            mExchangeDetailModelList.addAll(exchangeDetailList);
            if (exchangeDetailList.size() < Client.DEFAULT_PAGE_SIZE) {
                mLoadMore = false;
            } else {
                mLoadMore = true;
                mPage++;
            }
            mExchangeDetailAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class ExchangeDetailAdapter extends RecyclerView.Adapter<ExchangeDetailAdapter.ViewHolder> {

        public static final int HAS_STICKY_VIEW = 2;
        public static final int NONE_STICKY_VIEW = 3;
        private ArrayList<ExchangeDetailModel> mExchangeDetailArrayList;
        private Context mContext;

        public ExchangeDetailAdapter(ArrayList<ExchangeDetailModel> detailArrayList, Context context) {
            this.mExchangeDetailArrayList = detailArrayList;
            this.mContext = context;
        }

        public void addAll(ArrayList<ExchangeDetailModel> detailArrayList) {
            this.addAll(detailArrayList);
            notifyItemRangeInserted(mExchangeDetailArrayList.size() - detailArrayList.size(), mExchangeDetailArrayList.size());
        }

        public void clear() {
            mExchangeDetailArrayList.clear();
            notifyItemRangeRemoved(0, mExchangeDetailArrayList.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mExchangeDetailArrayList.get(position), position, isTheDifferentMonth((position)), mContext);
            if (isTheDifferentMonth(position)) {
                holder.itemView.setTag(HAS_STICKY_VIEW);
            } else {
                holder.itemView.setTag(NONE_STICKY_VIEW);
            }
            holder.itemView.setContentDescription(DateUtil.getFormatMonth(mExchangeDetailArrayList.get(position).getCreateTime()));
        }

        @Override
        public int getItemCount() {
            return mExchangeDetailArrayList != null ? mExchangeDetailArrayList.size() : 0;
        }

        private boolean isTheDifferentMonth(int position) {
            if (position == 0) {
                return true;
            }
            ExchangeDetailModel pre = mExchangeDetailArrayList.get(position - 1);
            ExchangeDetailModel next = mExchangeDetailArrayList.get(position);
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

            public void bindDataWithView(ExchangeDetailModel detail, int position, boolean theDifferentMonth, Context context) {
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
