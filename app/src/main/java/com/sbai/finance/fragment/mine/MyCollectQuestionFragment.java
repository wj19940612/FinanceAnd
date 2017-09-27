package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.MyCollect;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.NumberFormatUtils;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MyCollectQuestionFragment extends BaseFragment {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(R.id.listEmptyView)
    ListEmptyView mListEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    Unbinder unbinder;

    private int mPage;
    private HashSet<Integer> mSet;
    private MyCollectQuestionAdapter mMyCollectQuestionAdapter;

    public MyCollectQuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_or_comment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        requestMyQuestionCollect();
    }


    private void initView() {
        mSet = new HashSet<>();
        initListEmptyView();
        mList.setEmptyView(mListEmptyView);
        mMyCollectQuestionAdapter = new MyCollectQuestionAdapter(getActivity());
        mList.setAdapter(mMyCollectQuestionAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCollect question = (MyCollect) parent.getAdapter().getItem(position);
                if (question != null) {
                    Launcher.with(getActivity(), QuestionDetailActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, question.getId())
                            .execute();
                }
            }
        });

        initSwipeRefreshLayout();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                requestMyQuestionCollect();
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestMyQuestionCollect();
            }
        });
    }

    private void initListEmptyView() {
        mListEmptyView.setGoingText(R.string.look_miss);
        mListEmptyView.setContentText(R.string.you_not_has_comment_collection);
        mListEmptyView.setHintText(R.string.collect_favorite_miss_take_here);
        mListEmptyView.setOnGoingViewClickListener(new ListEmptyView.OnGoingViewClickListener() {
            @Override
            public void onGoingViewClick() {
                Launcher.with(getActivity(), MainActivity.class).putExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, 1).execute();
                getActivity().finish();
            }
        });
    }

    private void requestMyQuestionCollect() {
        Client.requestMyCollection(MyCollect.COLLECTI_TYPE_QUESTION, mPage)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<MyCollect>>, List<MyCollect>>() {
                    @Override
                    protected void onRespSuccessData(List<MyCollect> data) {
                        updateQuestionCollectionList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateQuestionCollectionList(List<MyCollect> data) {
        if (data == null) return;
        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (mSet.isEmpty()) {
            mMyCollectQuestionAdapter.clear();
        }

        for (MyCollect result : data) {
            if (mSet.add(result.getId())) {
                mMyCollectQuestionAdapter.add(result);
            }
        }
    }


    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    static class MyCollectQuestionAdapter extends ArrayAdapter<MyCollect> {

        private Context mContext;

        public MyCollectQuestionAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mine_question_or_answer, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.title)
            AppCompatTextView mTitle;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.time)
            TextView mTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(MyCollect question, Context context) {
                if (question == null) return;

                mTime.setText(DateUtil.formatDefaultStyleTime(question.getCreateTime()));

                String priseCount = NumberFormatUtils.formatTenThousandNumber(question.getPriseCount());
                String replyCount = NumberFormatUtils.formatTenThousandNumber(question.getReplyCount());
                String awardCount = NumberFormatUtils.formatTenThousandNumber(question.getAwardCount());
                mContent.setText(context.getString(R.string.question_replay_content, priseCount, replyCount, awardCount));
                mTitle.setText(question.getContent());
            }
        }
    }
}
