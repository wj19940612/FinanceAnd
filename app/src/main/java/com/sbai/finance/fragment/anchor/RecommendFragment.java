package com.sbai.finance.fragment.anchor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.anchor.AnchorPoint;
import com.sbai.finance.model.anchor.MissSwitcherModel;
import com.sbai.finance.model.anchor.Question;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.MissRadioViewSwitcher;
import com.sbai.finance.view.ThreeImageLayout;
import com.sbai.finance.view.radio.AnchorRecommendRadioLayout;

import java.util.ArrayList;
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
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    private Unbinder mBind;
    private HashSet<Integer> mSet;
    private RecommendPointAdapter mRecommendPointAdapter;

    private AnchorRecommendRadioLayout mAnchorRecommendRadioLayout;
    private MissRadioViewSwitcher mMissRadioViewSwitcher;
    private LinearLayout mPointLL;

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

        mListView.setAdapter(mRecommendPointAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        refreshData();
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
    }

    private void requestRecommendPoint() {
        Client.requestRecommendPoint()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<AnchorPoint>>, List<AnchorPoint>>() {
                    @Override
                    protected void onRespSuccessData(List<AnchorPoint> data) {
                        updateAnchorPointList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
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
            if (i % 2 == 0) {
                anchorPoint.setImageContent("http://d.hiphotos.baidu.com/image/pic/item/3801213fb80e7beca7ccb433252eb9389a506bca.jpg,http://f.hiphotos.baidu.com/image/pic/item/0bd162d9f2d3572c3c8859318013632763d0c3f1.jpg,http://d.hiphotos.baidu.com/image/pic/item/b90e7bec54e736d1040cf5e091504fc2d562693e.jpg");
            }
            pointArrayList.add(anchorPoint);
        }

        updateAnchorPointList(pointArrayList);
    }

    private void updateAnchorPointList(List<AnchorPoint> data) {
        if (mSet.isEmpty()) {
            mRecommendPointAdapter.clear();
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

    static class RecommendPointAdapter extends ArrayAdapter<AnchorPoint> {

        private Context mContext;

        public RecommendPointAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
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
            viewHolder.bindDataWithView(getItem(position), position, mContext);
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

            public void bindDataWithView(AnchorPoint anchorPoint, int position, Context context) {
                mAnchorInfoLayout.setVisibility(View.VISIBLE);
                mHasLabelLayout.setAvatar(anchorPoint.getAnchorPortrait(), Question.USER_IDENTITY_MISS);
                mAnchorName.setText(anchorPoint.getAnchorName());

                mPointTitle.setText(anchorPoint.getPointTitle());
                mPointContent.setText(anchorPoint.getPointContent());
                mThreeImageLayout.setImagePath(anchorPoint.getImageContent());
                if (anchorPoint.getPrise() == 0) {
                    mPrise.setText(R.string.praise);
                    mPrise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
                } else {
                    mPrise.setText(StrFormatter.getFormatCount(anchorPoint.getPrise()));
                    mPrise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
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
