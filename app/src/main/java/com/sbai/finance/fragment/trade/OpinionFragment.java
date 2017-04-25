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
    private List<Opinion> mOpinionList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opinion, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOpinionList = new ArrayList<>();
        initData();
        mOpinionAdapter = new OpinionAdapter(R.layout.row_opinion, mOpinionList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mOpinionAdapter);
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mOpinionList.add(new Opinion("哈哈"));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class OpinionAdapter extends BaseQuickAdapter<Opinion, BaseViewHolder> {

        private OpinionAdapter(int layoutResId, List<Opinion> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Opinion item) {
            helper.setText(R.id.userName, "张三丰")
                    .setText(R.id.followed, "已关注")
                    .setText(R.id.publishTime, "2008/08/08 08:00")
                    .setText(R.id.opinionType, R.string.bullish)
                    .setText(R.id.opinion, "           黄金涨涨涨")
                    .setText(R.id.commentNum, "8888")
                    .setText(R.id.likeNum, "8888");
        }
    }
}
