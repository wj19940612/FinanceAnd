package com.sbai.finance.fragment.anchor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.anchor.CommentActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.anchor.AnchorPoint;
import com.sbai.finance.model.anchor.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.ThreeImageLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 主播观点
 */
public class AnchorPointFragment extends BaseFragment {

    public static final String POINT_TYPE_KEY = "point_type_key";

    public static final int POINT_TYPE_RECOMMEND = 0;  //米圈推荐的观点
    public static final int POINT_TYPE_ANCHOR = 1;  //主播的观点

    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.emptyRecyclerView)
    EmptyRecyclerView mEmptyRecyclerView;


    private int mPointType;
    private Unbinder mBind;
    private HashSet<Integer> mSet;
    private AnchorPointAdapter mAnchorPointAdapter;
    private int mCustomId;
    private int mPage;

    private boolean mLoadMore;
    private ArrayList<AnchorPoint> mAnchorPointArrayList;

    public AnchorPointFragment() {
    }


    public static AnchorPointFragment newInstance(int pointType) {
        AnchorPointFragment fragment = new AnchorPointFragment();
        Bundle args = new Bundle();
        args.putInt(POINT_TYPE_KEY, pointType);
        fragment.setArguments(args);
        return fragment;
    }

    public AnchorPointFragment setAnchorId(int customId) {
        mCustomId = customId;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPointType = getArguments().getInt(POINT_TYPE_KEY, POINT_TYPE_RECOMMEND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_point, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        mAnchorPointArrayList = new ArrayList<>();

        initRecycleView();

        requestRecommendPoint();
    }

    private void initRecycleView() {
        mAnchorPointAdapter = new AnchorPointAdapter(mAnchorPointArrayList, getActivity());
        mAnchorPointAdapter.setPointType(POINT_TYPE_ANCHOR);
        mEmptyRecyclerView.setEmptyView(mEmpty);
        mEmptyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEmptyRecyclerView.setAdapter(mAnchorPointAdapter);

        mEmptyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL)) {
                    requestRecommendPoint();
                }

            }
        });

        initListener();
    }

    private void initListener() {
        mAnchorPointAdapter.setOnPointCallBack(new AnchorPointAdapter.OnPointCallBack() {
            @Override
            public void onItemClick(AnchorPoint anchorPoint, int position) {
                requestPointDetail(anchorPoint, position);
            }

            @Override
            public void onPraise(AnchorPoint anchorPoint, int position) {
                praisePoint(anchorPoint, position);
            }

            @Override
            public void onReview(AnchorPoint anchorPoint, int position) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), CommentActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD_1, anchorPoint.getId())
                            .putExtra(ExtraKeys.COMMENT_SOURCE, CommentActivity.COMMENT_TYPE_POINT)
                            .executeForResult(CommentActivity.REQ_CODE_COMMENT);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
    }

    private void praisePoint(AnchorPoint anchorPoint, int position) {
        if (LocalUser.getUser().isLogin()) {
            // TODO: 2018/1/3 点赞
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void requestPointDetail(final AnchorPoint anchorPoint, final int position) {
        if (anchorPoint != null) {
            Client.requestPointDetail(anchorPoint.getId())
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<AnchorPoint>, AnchorPoint>() {
                        @Override
                        protected void onRespSuccessData(AnchorPoint data) {
                            // TODO: 2018/1/3 h5详情  
                        }

                        @Override
                        protected void onRespFailure(Resp failedResp) {
                            super.onRespFailure(failedResp);
                            if (failedResp.getCode() == Resp.CODE_POINT_ALREADY_BUY) {
                                ToastUtil.show(failedResp.getMsg());
                            } else if (failedResp.getCode() == Resp.CODE_POINT_IS_DELETE || failedResp.getCode() == Resp.CODE_POINT_IS_SOLD_OUT) {
                                mAnchorPointArrayList.remove(anchorPoint);
                                mAnchorPointAdapter.notifyItemRemoved(position);
                            }
                        }
                    })
                    .fire();

        }
    }

    public void refreshData() {
        mSet.clear();
        mLoadMore = true;
        requestRecommendPoint();
    }

    private void requestRecommendPoint() {
        Client.requestAnchorPoint(mPage, mCustomId)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<AnchorPoint>>, List<AnchorPoint>>() {
                    @Override
                    protected void onRespSuccessData(List<AnchorPoint> data) {
                        updateAnchorPointList(data);
                    }
                })
                .fireFree();
    }

    private void updateAnchorPointList(List<AnchorPoint> data) {
        if (mSet.isEmpty()) {
            mAnchorPointAdapter.clear();
        }

        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mLoadMore = false;
        } else {
            mLoadMore = true;
            mPage++;
        }

        for (AnchorPoint result : data) {
            if (mSet.add(result.getId())) {
                mAnchorPointAdapter.add(result);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == BaseActivity.RESULT_OK) {
            switch (requestCode) {
                case CommentActivity.REQ_CODE_COMMENT:
                    if (data != null) {
                        int id = data.getIntExtra(ExtraKeys.QUESTION_ID, -1);
                        if (mAnchorPointArrayList != null && !mAnchorPointArrayList.isEmpty())
                            for (AnchorPoint result : mAnchorPointArrayList) {
                                if (result.getId() == id) {
                                    result.setCommentCount(1);
                                    mAnchorPointAdapter.notifyDataSetChanged();
                                }
                            }
                    }
                    break;
            }
        }
    }

    static class AnchorPointAdapter extends RecyclerView.Adapter<AnchorPointAdapter.ViewHolder> {

        public interface OnPointCallBack {
            void onItemClick(AnchorPoint anchorPoint, int position);

            void onPraise(AnchorPoint anchorPoint, int position);

            void onReview(AnchorPoint anchorPoint, int position);
        }

        private ArrayList<AnchorPoint> mAnchorPointArrayList;
        private Context mContext;
        private int mPointType;
        private OnPointCallBack mOnPointCallBack;

        public AnchorPointAdapter(ArrayList<AnchorPoint> anchorPointArrayList, @NonNull Context context) {
            mContext = context;
            mAnchorPointArrayList = anchorPointArrayList;
        }


        public void add(AnchorPoint anchorPoint) {
            mAnchorPointArrayList.add(anchorPoint);
            notifyDataSetChanged();
        }

        public void clear() {
            mAnchorPointArrayList.clear();
            notifyDataSetChanged();
        }

        public void setPointType(int pointType) {
            mPointType = pointType;
        }

        public void setOnPointCallBack(OnPointCallBack onPointCallBack) {
            mOnPointCallBack = onPointCallBack;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_anchor_point, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mAnchorPointArrayList.get(position), position, mContext, mPointType, mOnPointCallBack);
        }

        @Override
        public int getItemCount() {
            return mAnchorPointArrayList != null ? mAnchorPointArrayList.size() : 0;
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
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
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final AnchorPoint anchorPoint, final int position, Context context, int pointType, final OnPointCallBack onPointCallBack) {
                if (pointType == AnchorPointFragment.POINT_TYPE_ANCHOR) {
                    mAnchorInfoLayout.setVisibility(View.GONE);
                } else {
                    mAnchorInfoLayout.setVisibility(View.VISIBLE);
                    mHasLabelLayout.setAvatar(anchorPoint.getPortrait(), Question.USER_IDENTITY_MISS);
                    mAnchorName.setText(anchorPoint.getName());
                }

                mPointTitle.setText(anchorPoint.getViewTitle());
                mPointContent.setText(anchorPoint.getViewDesc());
                mThreeImageLayout.setImagePath(anchorPoint.getImgUrls());
                mPointPublishTime.setText(DateUtil.formatDefaultStyleTime(anchorPoint.getUpdateTime()));

                if (anchorPoint.getFree() == AnchorPoint.PRODUCT_RATE_FREE) {
                    mNeedPay.setVisibility(View.INVISIBLE);
                } else {
                    boolean alreadyPay = anchorPoint.getUserUse() == AnchorPoint.PRODUCT_RECHARGE_STATUS_ALREADY_PAY;
                    mNeedPay.setSelected(alreadyPay);
                }

                // TODO: 2018/1/4 是否点赞
                if (true) {
                    mPrise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
                } else {
                    mPrise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
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

                mReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onPointCallBack != null) {
                            if (anchorPoint.getCommentCount() == 0) {
                                onPointCallBack.onReview(anchorPoint, position);
                            } else {
                                onPointCallBack.onItemClick(anchorPoint, position);
                            }
                        }
                    }
                });
            }
        }
    }
}
