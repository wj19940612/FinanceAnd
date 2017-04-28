package com.sbai.finance.fragment.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sbai.finance.utils.Launcher;

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

    private OpinionAdapter mOpinionAdapter;
    private List<Opinion.OpinionBean> mOpinionList;

    private int mPage = 0;
    private int mPageSize = 15;
    private Variety mVariety;

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
        mOpinionAdapter = new OpinionAdapter(R.layout.row_opinion, mOpinionList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mOpinionAdapter);
        getPointList();
    }


    private void getPointList() {
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

    public void refreshPointList(){
        getPointList();
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
            String trend = item.getDirection() == 1 ? getString(R.string.bullish) : getString(R.string.bearish);
            helper.setText(R.id.userName, item.getUserName())
                    .setText(R.id.followed, attend)
                    .setText(R.id.publishTime, time)
                    .setText(R.id.opinionType, trend)
                    .setText(R.id.opinion, item.getContent())
                    .setText(R.id.commentNum, item.getReplyCount())
                    .setText(R.id.likeNum, item.getPraiseCount());
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
