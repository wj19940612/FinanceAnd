package com.sbai.finance.fragment.anchor;

import android.content.Context;
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

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.anchor.AnchorPoint;
import com.sbai.finance.model.anchor.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.StrFormatter;
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

    public AnchorPointFragment() {
    }


    public static AnchorPointFragment newInstance(int pointType) {
        AnchorPointFragment fragment = new AnchorPointFragment();
        Bundle args = new Bundle();
        args.putInt(POINT_TYPE_KEY, pointType);
        fragment.setArguments(args);
        return fragment;
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

        mAnchorPointAdapter = new AnchorPointAdapter(new ArrayList<AnchorPoint>(), getActivity());
        mAnchorPointAdapter.setPointType(POINT_TYPE_ANCHOR);
        mEmptyRecyclerView.setEmptyView(mEmpty);
        mEmptyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEmptyRecyclerView.setAdapter(mAnchorPointAdapter);

        requestRecommendPoint();
    }

    public void refreshData() {
        mSet.clear();
        requestRecommendPoint();
    }

    private void requestRecommendPoint() {
        Client.requestRecommendPoint()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<AnchorPoint>>, List<AnchorPoint>>() {
                    @Override
                    protected void onRespSuccessData(List<AnchorPoint> data) {
                        updateAnchorPointList(data);
                    }
                })
                .fireFree();

        // TODO: 2018/1/2 模拟数据
        ArrayList<AnchorPoint> pointArrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            AnchorPoint anchorPoint = new AnchorPoint();
            anchorPoint.setId(i);
            anchorPoint.setAnchorName(i + " 溺水的鱼 " + i + " 号 ");
            anchorPoint.setAnchorPortrait("http://img.zcool.cn/community/0117e2571b8b246ac72538120dd8a4.jpg@1280w_1l_2o_100sh.jpg");
            anchorPoint.setPointContent("肯定撒付款的时间阿卡丽点时空裂缝建档立卡手机开了莱克斯顿荆防颗粒的手机" + i + "\n 客户端空间是否会尽快回答是否可结合当升科技复合大师可见\n dadasd ");
            anchorPoint.setPointTitle("这是第  " + i + " 推荐观点");
            anchorPoint.setTime(System.currentTimeMillis());
            anchorPoint.setImageContent("http://d.hiphotos.baidu.com/image/pic/item/3801213fb80e7beca7ccb433252eb9389a506bca.jpg,http://f.hiphotos.baidu.com/image/pic/item/0bd162d9f2d3572c3c8859318013632763d0c3f1.jpg,http://d.hiphotos.baidu.com/image/pic/item/b90e7bec54e736d1040cf5e091504fc2d562693e.jpg");
            pointArrayList.add(anchorPoint);
        }

        updateAnchorPointList(pointArrayList);
    }

    private void updateAnchorPointList(List<AnchorPoint> data) {
        if (mSet.isEmpty()) {
            mAnchorPointAdapter.clear();
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


    static class AnchorPointAdapter extends RecyclerView.Adapter<AnchorPointAdapter.ViewHolder> {

        private ArrayList<AnchorPoint> mAnchorPointArrayList;
        private Context mContext;
        private int mPointType;

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


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_anchor_point, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mAnchorPointArrayList.get(position), position, mContext, mPointType);
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

            public void bindDataWithView(AnchorPoint anchorPoint, int position, Context context, int pointType) {
                if (pointType == AnchorPointFragment.POINT_TYPE_ANCHOR) {
                    mAnchorInfoLayout.setVisibility(View.GONE);
                } else {

                    mAnchorInfoLayout.setVisibility(View.VISIBLE);
                    mHasLabelLayout.setAvatar(anchorPoint.getAnchorPortrait(), Question.USER_IDENTITY_MISS);
                    mAnchorName.setText(anchorPoint.getAnchorName());
                }

                mPointTitle.setText(anchorPoint.getPointTitle());
                mPointContent.setText(anchorPoint.getPointContent());
                mThreeImageLayout.setImagePath(anchorPoint.getImageContent());
                if (anchorPoint.getPrise() == 0) {
                    mPrise.setText(R.string.praise);
                } else {
                    mPrise.setText(StrFormatter.getFormatCount(anchorPoint.getPrise()));
                }
                mPrise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 2018/1/2 点赞

                    }
                });

                if (anchorPoint.getReview() == 0) {
                    mReview.setText(R.string.comment);
                } else {
                    mReview.setText(StrFormatter.getFormatCount(anchorPoint.getReview()));
                }

                mReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 2018/1/2  评论
                    }
                });

                mPointPublishTime.setText(DateUtil.formatDefaultStyleTime(anchorPoint.getTime()));
            }
        }
    }
}
