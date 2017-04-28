package com.sbai.finance.fragment.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.Opinion;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OpinionFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    Unbinder unbinder;
    TextView mFootView;
    View mBottomView;

    private OpinionAdapter mOpinionAdapter;
    private List<Opinion.OpinionBean> mOpinionList;

    private int mPage = 0;
    private int mPageSize = 15;
    private Variety mVariety;
    boolean mLoadMore = true;

    public static OpinionFragment newInstance(Bundle args) {
        OpinionFragment fragment = new OpinionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opinion, container, false);
        unbinder = ButterKnife.bind(this, view);
        mVariety = getArguments().getParcelable(Launcher.EX_PAYLOAD);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOpinionList = new ArrayList<>();
        addTestList();
        initViewWithAdapter();
        getPointList();

    }

    private void initViewWithAdapter() {
        mBottomView = new View(getActivity());
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) Display.dp2Px(55,getResources()));
        mBottomView.setLayoutParams(layoutParams);

        mFootView = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        mFootView.setPadding(padding, padding, padding, 4 * padding);
        mFootView.setText(getText(R.string.load_all));
        mFootView.setGravity(Gravity.CENTER);
        mFootView.setTextColor(ContextCompat.getColor(getActivity(), R.color.secondaryText));
        mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.greyLightAssist));

        mOpinionAdapter = new OpinionAdapter(R.layout.row_opinion, mOpinionList);
        mOpinionAdapter.setFooterView(mBottomView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mOpinionAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isSlideToBottom(recyclerView) && mLoadMore) {
                getPointList();
            }
        }
    };

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }


    private void addTestList() {
        //测试数据
        for (int i = 0; i < 10; i++) {
            Opinion.OpinionBean opinionBean = new Opinion.OpinionBean();
            opinionBean.setIsAttention(2);
            opinionBean.setCreateTime(System.currentTimeMillis());
            opinionBean.setUserName("李诚信");
            opinionBean.setDirection(1);
            opinionBean.setContent("蒙古海军总司令,蒙古海军总司令");
            opinionBean.setReplyCount(9999);
            opinionBean.setPraiseCount(8888);
            mOpinionList.add(opinionBean);
        }
    }

    private void getPointList() {
        Client.findViewpoint(mPage, mPageSize, mVariety.getVarietyId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Opinion.OpinionBean>>,List<Opinion.OpinionBean>>() {
                    @Override
                    protected void onRespSuccessData(List<Opinion.OpinionBean> data) {
                        updateViewWithData(data);
                    }

                })
                .fire();
    }

    private void updateViewWithData(List<Opinion.OpinionBean> data) {
        if (data != null && data.size() > 0) {
            mOpinionList.addAll(data);
            if (data.size()<mPageSize){
                mOpinionAdapter.removeFooterView(mBottomView);
                mOpinionAdapter.addFooterView(mFootView);
                mLoadMore = false;
            }
            mOpinionAdapter.notifyDataSetChanged();
        }
    }

    private void getNewPointList() {
        mPage = 0;
        Client.findViewpoint(mPage, mPageSize, mVariety.getVarietyId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Opinion>, Opinion>() {
                    @Override
                    protected void onRespSuccessData(Opinion data) {
                        mOpinionList = data.getData();
                        mOpinionAdapter.notifyDataSetChanged();
                    }
                })
                .fire();
    }

    public void refreshPointList() {
        getNewPointList();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private class OpinionAdapter extends BaseQuickAdapter<Opinion.OpinionBean, BaseViewHolder> {

        private OpinionAdapter(int layoutResId, List<Opinion.OpinionBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Opinion.OpinionBean item) {
            bindDataWithView(helper, item);
        }

        private void bindDataWithView(BaseViewHolder helper, Opinion.OpinionBean item) {
            String attend = item.getIsAttention() == 1 ? "" : getString(R.string.is_attention);
            String format = "yyyy/MM/dd HH:mm";
            String time = DateUtil.format(item.getCreateTime(), format);
            helper.setText(R.id.userName, item.getUserName())
                    .setText(R.id.followed, attend)
                    .setText(R.id.publishTime, time)
                    .setText(R.id.commentNum, String.valueOf(item.getReplyCount()))
                    .setText(R.id.likeNum, String.valueOf(item.getPraiseCount()));
            if (item.getDirection() == 1) {
                ((TextView) helper.getView(R.id.opinion))
                        .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_up));
            } else {
                ((TextView) helper.getView(R.id.opinion))
                        .setText(StrUtil.mergeTextWithImage(getActivity(), item.getContent(), R.drawable.ic_opinion_down));
            }
            helper.getView(R.id.avatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/4/28  用户详情
                }
            });
            helper.getView(R.id.contentRl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/4/28 观点详情
                }
            });
            helper.getView(R.id.commentRl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/4/28 观点详情
                }
            });
        }
    }
}
