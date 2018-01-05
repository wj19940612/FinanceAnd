package com.sbai.finance.fragment.anchor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.anchor.CommentActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.anchor.AnchorPoint;
import com.sbai.finance.model.anchor.MissSwitcherModel;
import com.sbai.finance.model.anchor.Praise;
import com.sbai.finance.model.anchor.Question;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.MissRadioViewSwitcher;
import com.sbai.finance.view.ThreeImageLayout;
import com.sbai.finance.view.radio.AnchorRecommendRadioLayout;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/12/28.
 * 米圈推荐页面
 */

public class RecommendFragment extends BaseFragment {


    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    private Unbinder mBind;
    private HashSet<Integer> mSet;
    private RecommendPointAdapter mRecommendPointAdapter;

    private AnchorRecommendRadioLayout mAnchorRecommendRadioLayout;
    private MissRadioViewSwitcher mMissRadioViewSwitcher;
    private LinearLayout mPointLL;

    private int mPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recomend, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();

        mRecommendPointAdapter = new RecommendPointAdapter(getActivity());

        createListHeadView();

        initView();

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void initView() {
        mListView.setAdapter(mRecommendPointAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestRecommendPoint();
            }
        });
        initListener();
    }

    private void initListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                AnchorPoint anchorPoint = (AnchorPoint) parent.getAdapter().getItem(position);
                requestPointDetail(anchorPoint);
            }
        });


        mRecommendPointAdapter.setOnPointCallBack(new AnchorPointFragment.AnchorPointAdapter.OnPointCallBack() {
            @Override
            public void onItemClick(AnchorPoint anchorPoint, int position) {

            }

            @Override
            public void onPraise(AnchorPoint anchorPoint, int position) {
                praisePoint(anchorPoint);
            }

            @Override
            public void onReview(AnchorPoint anchorPoint, int position) {
                if (LocalUser.getUser().isLogin()) {
                    Intent intent = new Intent();
                    intent.putExtra(Launcher.EX_PAYLOAD_1, anchorPoint.getId());
                    intent.putExtra(ExtraKeys.COMMENT_SOURCE, CommentActivity.COMMENT_TYPE_POINT);
                    intent.setClass(getActivity(), CommentActivity.class);
                    getActivity().startActivityForResult(intent, CommentActivity.REQ_CODE_COMMENT);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
    }

    private void praisePoint(final AnchorPoint anchorPoint) {
        if (LocalUser.getUser().isLogin()) {
            Client.praisePoint(anchorPoint.getId())
                    .setIndeterminate(this)
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<Praise>, Praise>() {
                        @Override
                        protected void onRespSuccessData(Praise data) {
                            anchorPoint.setPraiseCount(data.getPriseCount());
                            anchorPoint.setPraise(data.getIsPrise());
                            mRecommendPointAdapter.notifyDataSetChanged();
                        }
                    })
                    .fire();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void requestPointDetail(final AnchorPoint anchorPoint) {
        if (anchorPoint != null) {
            Client.requestPointDetail(anchorPoint.getId())
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<AnchorPoint>, AnchorPoint>() {
                        @Override
                        protected void onRespSuccessData(AnchorPoint data) {
                            if (data.getFree() == AnchorPoint.PRODUCT_RATE_CHARGE
                                    && data.getUserUse() == AnchorPoint.PRODUCT_RECHARGE_STATUS_NOT_PAY) {
                                if (LocalUser.getUser().isLogin()) {
                                    Launcher.openBuyPage(getActivity(), data);
                                } else {
                                    Launcher.with(getActivity(), LoginActivity.class).execute();
                                }
                            } else {
                                String url = String.format(Client.PAGE_URL_POINT_DETAIL, data.getId());
                                Launcher.with(getActivity(), WebActivity.class).putExtra(WebActivity.EX_URL, url).execute();
                            }
                        }

                        @Override
                        protected void onRespFailure(Resp failedResp) {
                            super.onRespFailure(failedResp);
                            if (failedResp.getCode() == Resp.CODE_POINT_ALREADY_BUY) {
                                ToastUtil.show(failedResp.getMsg());
                            } else if (failedResp.getCode() == Resp.CODE_POINT_IS_DELETE || failedResp.getCode() == Resp.CODE_POINT_IS_SOLD_OUT) {
                                ToastUtil.show(failedResp.getMsg());
                                mRecommendPointAdapter.remove(anchorPoint);
                            }
                        }
                    })
                    .fire();

        }
    }

    private void createListHeadView() {
        View view = getLayoutInflater().inflate(R.layout.view_recommend_head, null);
        mAnchorRecommendRadioLayout = view.findViewById(R.id.missRadioLayout);
        mMissRadioViewSwitcher = view.findViewById(R.id.missRadioViewSwitcher);
        mPointLL = view.findViewById(R.id.pointLL);
        mListView.addHeaderView(view);
    }

    public void refreshData() {
        mSet.clear();
        requestMissSwitcherList();
        requestRadioList();
        requestRecommendPoint();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    private void requestRecommendPoint() {
        Client.requestAnchorPoint(mPage, 0)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<AnchorPoint>>, List<AnchorPoint>>() {
                    @Override
                    protected void onRespSuccessData(List<AnchorPoint> data) {
                        updateAnchorPointList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fireFree();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    private void updateAnchorPointList(List<AnchorPoint> data) {
        if (mSet.isEmpty()) {
            mRecommendPointAdapter.clear();
        }

        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mSwipeRefreshLayout.setLoadMoreEnable(true);
            mPage++;
        }

        for (AnchorPoint result : data) {
            if (mSet.add(result.getId())) {
                mRecommendPointAdapter.add(result);
            }
        }
    }

    private void requestMissSwitcherList() {
        Client.requestMissSwitcherList()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MissSwitcherModel>>, List<MissSwitcherModel>>() {
                    @Override
                    protected void onRespSuccessData(List<MissSwitcherModel> data) {
                        mMissRadioViewSwitcher.setSwitcherData(data);
                    }
                })
                .fireFree();
    }

    public void requestRadioList() {
        Client.requestRadioList()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Radio>>, List<Radio>>() {
                    @Override
                    protected void onRespSuccessData(List<Radio> data) {
                        mAnchorRecommendRadioLayout.setMissRadioList(data);
                    }
                })
                .fireFree();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    public void updatePointComment(int id) {
        for (int i = 0; i < mRecommendPointAdapter.getCount(); i++) {
            AnchorPoint anchorPoint = mRecommendPointAdapter.getItem(i);
            if (anchorPoint != null && anchorPoint.getId() == id) {
                anchorPoint.setCommentCount(1);
                mRecommendPointAdapter.notifyDataSetChanged();
            }
        }
    }

    static class RecommendPointAdapter extends ArrayAdapter<AnchorPoint> {

        private Context mContext;
        private AnchorPointFragment.AnchorPointAdapter.OnPointCallBack mOnPointCallBack;

        public RecommendPointAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        public void setOnPointCallBack(AnchorPointFragment.AnchorPointAdapter.OnPointCallBack onPointCallBack) {
            mOnPointCallBack = onPointCallBack;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_anchor_point, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), position, mContext, mOnPointCallBack);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.needPay)
            ImageView mNeedPay;
            @BindView(R.id.hasLabelLayout)
            HasLabelImageLayout mHasLabelLayout;
            @BindView(R.id.anchorName)
            TextView mAnchorName;
            @BindView(R.id.anchorInfoLayout)
            LinearLayout mAnchorInfoLayout;
            @BindView(R.id.pointTitle)
            TextView mPointTitle;
            @BindView(R.id.pointContent)
            TextView mPointContent;
            @BindView(R.id.threeImageLayout)
            ThreeImageLayout mThreeImageLayout;
            @BindView(R.id.split)
            View mSplit;
            @BindView(R.id.prise)
            TextView mPrise;
            @BindView(R.id.review)
            TextView mReview;
            @BindView(R.id.pointPublishTime)
            TextView mPointPublishTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final AnchorPoint anchorPoint, final int position, Context context, final AnchorPointFragment.AnchorPointAdapter.OnPointCallBack onPointCallBack) {
                mAnchorInfoLayout.setVisibility(View.VISIBLE);
                mHasLabelLayout.setAvatar(anchorPoint.getPortrait(), Question.USER_IDENTITY_MISS);
                mAnchorName.setText(anchorPoint.getName());
                mPointTitle.setText(anchorPoint.getViewTitle());
                mPointContent.setText(anchorPoint.getViewDesc());
                mThreeImageLayout.setImagePath(anchorPoint.getImgUrls());
                mPointPublishTime.setText(DateUtil.formatDefaultStyleTime(anchorPoint.getUpdateTime()));

                if (anchorPoint.getPraise() == Praise.IS_PRAISE) {
                    mPrise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
                } else {
                    mPrise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
                }

                if (anchorPoint.getFree() == AnchorPoint.PRODUCT_RATE_FREE) {
                    mNeedPay.setVisibility(View.INVISIBLE);
                } else {
                    mNeedPay.setVisibility(View.VISIBLE);
                    boolean alreadyPay = anchorPoint.getUserUse() == AnchorPoint.PRODUCT_RECHARGE_STATUS_ALREADY_PAY;
                    mNeedPay.setSelected(alreadyPay);
                }

                if (anchorPoint.getPraiseCount() == 0) {
                    mPrise.setText(R.string.praise);
                } else {
                    mPrise.setText(StrFormatter.getFormatCount(anchorPoint.getPraiseCount()));
                }

                if (anchorPoint.getCommentCount() == 0) {
                    mReview.setText(R.string.comment);
                } else {
                    mReview.setText(StrFormatter.getFormatCount(anchorPoint.getCommentCount()));
                }

                mPrise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onPointCallBack != null) {
                            onPointCallBack.onPraise(anchorPoint, position);
                        }
                    }
                });

                if (anchorPoint.getCommentCount() == 0) {
                    mReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPointCallBack.onReview(anchorPoint, position);
                        }
                    });
                }
            }
        }
    }

}
