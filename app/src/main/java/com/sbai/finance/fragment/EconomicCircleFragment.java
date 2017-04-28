package com.sbai.finance.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.model.EconomicCircle;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EconomicCircleFragment extends BaseFragment implements AbsListView.OnScrollListener {
    private static final int TYPE_OPINION = 0;
    private static final int TYPE_BORROW_MONEY = 1;

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private List<EconomicCircle> mEconomicCircleList;
    private EconomicCircleAdapter mEconomicCircleAdapter;
    private TextView mFootView;

    private int mPage = 0;
    private int mPageSize = 15;
    private HashSet<String> mSet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_economic_circle, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        mEconomicCircleList = new ArrayList<>();
        mEconomicCircleAdapter = new EconomicCircleAdapter(getContext(), mEconomicCircleList);
        mListView.setEmptyView(mEmpty);
//        mListView.setAdapter(mEconomicCircleAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position % 2 == 0) {
                    Launcher.with(getContext(), OpinionDetailsActivity.class)
                            .execute();
                } else {
                    Launcher.with(getContext(), BorrowMoneyDetailsActivity.class)
                            .execute();
                }
            }
        });

        requestEconomicCircleList();
        initSwipeRefreshLayout();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);

    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                requestEconomicCircleList();
            }
        });

    }

    private void requestEconomicCircleList() {
        Client.getEconomicCircleList(mPage, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<EconomicCircle>>, List<EconomicCircle>>() {
                    @Override
                    protected void onRespSuccessData(List<EconomicCircle> economicCircleList) {
                        mEconomicCircleList.clear();
                        mEconomicCircleList.addAll(economicCircleList);
                        sortEconomicCircleList(mEconomicCircleList);
                        updateEconomicCircleList(mEconomicCircleList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void sortEconomicCircleList(List<EconomicCircle> economicCircleList) {
        Collections.sort(economicCircleList, new Comparator<EconomicCircle>() {
            @Override
            public int compare(EconomicCircle o1, EconomicCircle o2) {
                return Long.valueOf(o2.getCreateTime() - o1.getCreateTime()).intValue();
            }
        });
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateEconomicCircleList(List<EconomicCircle> economicCircleList) {
        if (economicCircleList == null) {
            stopRefreshAnimation();
            return;
        }

        if (mFootView == null) {
            mFootView = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            mFootView.setPadding(padding, padding, padding, padding);
            mFootView.setText(getText(R.string.load_more));
            mFootView.setGravity(Gravity.CENTER);
            mFootView.setTextColor(Color.WHITE);
            mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;
                    mPage++;
                    requestEconomicCircleList();
                }
            });
            mListView.addFooterView(mFootView);
        }

        if (economicCircleList.size() < mPageSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mEconomicCircleAdapter != null) {
                mEconomicCircleAdapter.clear();
                mEconomicCircleAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }

        for (EconomicCircle economicCircle : economicCircleList) {
            if (mSet.add(economicCircle.getId())) {
                mEconomicCircleAdapter.addAll(economicCircle);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    static class EconomicCircleAdapter extends ArrayAdapter<EconomicCircle> {

        interface Callback {
            void onAvatarOpinionClick(EconomicCircle economicCircle);

            void onAvatarBorrowMoneyClick(EconomicCircle economicCircle);
        }

        private Context mContext;
        private Callback mCallback;
        private List<EconomicCircle> mEconomicCircleList;

        private EconomicCircleAdapter(Context context, List<EconomicCircle> economicCircleList) {
            super(context, 0);
            this.mContext = context;
            this.mEconomicCircleList = economicCircleList;
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return mEconomicCircleList.get(position).getType();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OpinionViewHolder opinionViewHolder;
            BorrowMoneyViewHolder borrowMoneyViewHolder;
                switch (getItemViewType(position)) {
                    case TYPE_OPINION:
                        if (convertView == null) {
                            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_opinion, null);
                            opinionViewHolder = new OpinionViewHolder(convertView);
                            convertView.setTag(opinionViewHolder);
                        } else {
                            opinionViewHolder = (OpinionViewHolder) convertView.getTag();
                        }

                        opinionViewHolder.bindingData(mContext, getItem(position), mCallback);
                        break;

                    case TYPE_BORROW_MONEY:
                        if (convertView == null) {
                            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_borrow_money, null);
                            borrowMoneyViewHolder = new BorrowMoneyViewHolder(convertView);
                            convertView.setTag(borrowMoneyViewHolder);
                        } else {
                            borrowMoneyViewHolder = (BorrowMoneyViewHolder) convertView.getTag();
                        }
                        borrowMoneyViewHolder.bindingData(mContext, getItem(position), mCallback);
                        break;
                }

            return convertView;
        }

        static class OpinionViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.isAttention)
            TextView mIsAttention;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.opinionContent)
            TextView mOpinionContent;
            @BindView(R.id.bigVarietyName)
            TextView mBigVarietyName;
            @BindView(R.id.varietyName)
            TextView mVarietyName;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.upDownPrice)
            TextView mUpDownPrice;
            @BindView(R.id.upDownPercent)
            TextView mUpDownPercent;

            OpinionViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, final EconomicCircle item, final Callback callback) {
                mUserName.setText(item.getUserName());
                if (item.getIsAttention() == 1) {
                    mIsAttention.setText("已关注");
                }
                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));

                if (item.getGuessPass() == 1 ) {
                    mOpinionContent.setText( StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_up));
                } else {
                    mOpinionContent.setText( StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_down));
                }

                mBigVarietyName.setText(item.getBigVarietyTypeName());
                mVarietyName.setText(item.getVarietyName());
                mLastPrice.setText("88.88");
                mUpDownPrice.setText("+8.8");
                mUpDownPercent.setText("+10%");
                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onAvatarOpinionClick(item);
                    }
                });

            }
        }

        static class BorrowMoneyViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.address)
            TextView mAddress;
            @BindView(R.id.needAmount)

            TextView mNeedAmount;
            @BindView(R.id.borrowTime)
            TextView mBorrowTime;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;
            @BindView(R.id.opinion)
            TextView mOpinion;

            BorrowMoneyViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, EconomicCircle item, Callback callback) {
                mUserName.setText(item.getUserName());
                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
                mAddress.setText("山东");
                mNeedAmount.setText(context.getString(R.string.RMB, "8888"));
                mBorrowTime.setText(context.getString(R.string.day, "8888"));
                mBorrowInterest.setText(context.getString(R.string.RMB, "8888"));
                mOpinion.setText("话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。");
            }
        }
    }
}
