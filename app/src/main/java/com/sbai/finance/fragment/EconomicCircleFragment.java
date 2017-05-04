package com.sbai.finance.fragment;

import android.content.Context;
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
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.EconomicCircle;
import com.sbai.finance.model.economiccircle.OpinionDetails;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.TitleBar;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EconomicCircleFragment extends BaseFragment implements AbsListView.OnScrollListener {

    private static final int TYPE_BORROW_MONEY = 1;
    private static final int TYPE_OPINION = 2;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

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
        addTopPaddingWithStatusBar(mTitleBar);
        mSet = new HashSet<>();
        mEconomicCircleAdapter = new EconomicCircleAdapter(getContext());
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mEconomicCircleAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EconomicCircle economicCircle = (EconomicCircle) parent.getItemAtPosition(position);
                if (economicCircle.getType() == TYPE_OPINION) {
                    if (LocalUser.getUser().isLogin()) {
                        Client.getOpinionDetails(economicCircle.getDataId()).setTag(TAG)
                                .setCallback(new Callback2D<Resp<OpinionDetails>, OpinionDetails>() {
                                    @Override
                                    protected void onRespSuccessData(OpinionDetails opinionDetails) {
                                        Launcher.with(getContext(), OpinionDetailsActivity.class)
                                                .putExtra(Launcher.EX_PAYLOAD, opinionDetails)
                                                .execute();
                                    }
                                }).fire();
                    }
                } else {
                    Launcher.with(getContext(), BorrowMoneyDetailsActivity.class)
                            .execute();
                }
            }
        });

        mEconomicCircleAdapter.setCallback(new EconomicCircleAdapter.Callback() {
            @Override
            public void onAvatarOpinionClick(EconomicCircle economicCircle) {
                Launcher.with(getContext(), UserDataActivity.class)
                        .putExtra("userId", economicCircle.getUserId())
                        .execute();
            }

            @Override
            public void onAvatarBorrowMoneyClick(EconomicCircle economicCircle) {
                Launcher.with(getContext(), UserDataActivity.class).execute();
            }
        });

        requestEconomicCircleList();
        initSwipeRefreshLayout();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            mSwipeRefreshLayout.setRefreshing(true);
            mSet.clear();
            mPage = 0;
            requestEconomicCircleList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(true);
        mSet.clear();
        mPage = 0;
        requestEconomicCircleList();
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
                        updateEconomicCircleList(economicCircleList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
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
            mFootView.setTextColor(ContextCompat.getColor(getContext(), R.color.greyAssist));
            mFootView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLightAssist));
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
                mEconomicCircleAdapter.add(economicCircle);
                mEconomicCircleAdapter.sort(new Comparator<EconomicCircle>() {
                    @Override
                    public int compare(EconomicCircle o1, EconomicCircle o2) {
                        return Long.valueOf(o2.getCreateTime() - o1.getCreateTime()).intValue();
                    }
                });
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

        private EconomicCircleAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType();
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
                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);

                mUserName.setText(item.getUserName());
                if (item.getIsAttention() == 2) {
                    mIsAttention.setText(R.string.is_attention);
                }
                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));


                if (item.getDirection() == 1) {
                    mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_up));
                } else {
                    mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_down));
                }

                mBigVarietyName.setText(item.getBigVarietyTypeName());
                mVarietyName.setText(item.getVarietyName());
                
                mLastPrice.setText("88.88");
                mUpDownPrice.setText("+8.8");
                mUpDownPercent.setText("+10%");

                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onAvatarOpinionClick(item);
                        }
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
            @BindView(R.id.location)
            TextView mLocation;
            @BindView(R.id.borrowMoneyContent)
            TextView mBorrowMoneyContent;
            @BindView(R.id.needAmount)

            TextView mNeedAmount;
            @BindView(R.id.borrowTime)
            TextView mBorrowTime;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;


            BorrowMoneyViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, final EconomicCircle item, final Callback callback) {
                mUserName.setText(item.getUserName());
                mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
                mLocation.setText(item.getLand());
                mNeedAmount.setText(context.getString(R.string.RMB,  String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day, String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB, String.valueOf(item.getInterest())));
                mBorrowMoneyContent.setText(item.getContent());
                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onAvatarBorrowMoneyClick(item);
                        }
                    }
                });
            }
        }
    }
}
