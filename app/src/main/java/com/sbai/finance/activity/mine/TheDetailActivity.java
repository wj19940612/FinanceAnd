package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Detail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class TheDetailActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private DetailAdapter mDetailAdapter;
    private List<Detail> mDetailList;

    private int mPageSize = 15;
    private int mPageNo = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initViews();
        requestDetailList();
    }

    private void initViews() {
        mDetailList = new ArrayList<>();
        mDetailAdapter = new DetailAdapter(this, mDetailList);
        mListView.setAdapter(mDetailAdapter);
        mListView.setEmptyView(mEmpty);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mDetailAdapter);
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
        if (detailList == null) {
            return;
        }
        stopRefreshAnimation();
        mDetailAdapter.addAll(detailList);
        if (detailList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPageNo++;
        }
        mDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        reset();
        requestDetailList();
    }

    @Override
    public void onLoadMore() {
        requestDetailList();
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
        mPageNo = 0;
        mDetailAdapter.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }


    static class DetailAdapter extends BaseAdapter {
        private Context mContext;
        private List<Detail> mList;

        public DetailAdapter(Context context, List<Detail> list) {
            mContext = context;
            mList = list;
        }

        public void addAll(List<Detail> detailList) {
            mList.addAll(detailList);
            notifyDataSetChanged();
        }

        public void clear() {
            mList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_detail, null, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            boolean needTitle = needTitle(position);
            holder.bindDataWithView((Detail) getItem(position), needTitle);
            return convertView;
        }

        private boolean needTitle(int position) {
            if (position == 0) {
                return true;
            }

            if (position < 0) {
                return false;
            }

            Detail pre = mList.get(position - 1);
            Detail next = mList.get(position);
            //判断两个时间在不在一个月内  不是就要显示标题
            long preTime = pre.getCreateTime();
            long nextTime = next.getCreateTime();
            if (DateUtil.isInThisMonth(nextTime, preTime)) {
                return false;
            }
            return true;
        }

        static class ViewHolder {
            @BindView(R.id.head)
            TextView mHead;
            @BindView(R.id.paymentContent)
            TextView mPaymentContent;
            @BindView(R.id.paymentTime)
            TextView mPaymentTime;
            @BindView(R.id.paymentType)
            TextView mPaymentType;
            @BindView(R.id.paymentNum)
            TextView mPaymentNum;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(Detail detail, boolean needTitle) {

                if (needTitle) {
                    mHead.setVisibility(View.VISIBLE);
                    mHead.setText(DateUtil.getFormatMonth(detail.getCreateTime()));
                } else {
                    mHead.setVisibility(View.GONE);
                }

                mPaymentTime.setText(DateUtil.getDetailFormatTime(detail.getCreateTime()));

                String payType = detail.getType() == 2 ? "微信" : "支付宝";

                mPaymentContent.setText(detail.getRemark());
                mPaymentType.setText(payType);
                mPaymentNum.setText(String.valueOf(detail.getMoney()) + "元");

            }
        }
    }
}
