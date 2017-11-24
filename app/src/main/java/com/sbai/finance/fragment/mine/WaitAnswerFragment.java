package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.content.Intent;
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
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.Answer;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class WaitAnswerFragment extends BaseFragment {

    private static final String ANSWER_TYPE = "answer_type";

    //待回答
    public static final int TYPE_WAIT_FOR_ANSWER = 1;
    //待抢答
    public static final int TYPE_WAIT_FOR_RACE_ANSWER = 2;
    //已回答
    public static final int TYPE_WAIT_FOR_HAVE_ANSWER = 3;

    private Unbinder mBind;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(R.id.listEmptyView)
    ListEmptyView mListEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private AnswerAdapter mAnswerAdapter;
    private int mPage;
    private int mAnswerType = 1;

    public static WaitAnswerFragment newInstance(int type) {
        WaitAnswerFragment fragment = new WaitAnswerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ANSWER_TYPE, 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnswerType = getArguments().getInt(ANSWER_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wait_answer, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void initView() {
        initListEmptyView();
        mListView.setEmptyView(mListEmptyView);
        mListView.setDivider(null);
        mAnswerAdapter = new AnswerAdapter(getActivity());
        mListView.setAdapter(mAnswerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
//                requestMineQuestionOrComment(false);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Question question = (Question) parent.getAdapter().getItem(position);
//                if (question != null && question.isQuestionSolved()) {
//                    mClickQuestion = question;
//                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
//                    intent.putExtra(Launcher.EX_PAYLOAD, question.getDataId());
//                    intent.putExtra(Launcher.EX_PAYLOAD_1, question.getCommentId());
//                    startActivityForResult(intent, QuestionDetailActivity.REQ_CODE_QUESTION_DETAIL);
//                }
            }
        });
    }

    private void refreshData() {
        mPage = 0;
        mSwipeRefreshLayout.setLoadMoreEnable(true);
        requestWaitForMeAnswer(true);
    }

    private void requestWaitForMeAnswer(final boolean isRefreshing) {
        //TODO 待回答请求
        Client.requestMineQuestionOrComment(1, mPage)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> data) {
//                        updateAnswerList(data, isRefreshing);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void initListEmptyView() {
        switch (mAnswerType) {
            case TYPE_WAIT_FOR_ANSWER:
                mListEmptyView.setContentText(R.string.you_not_has_answer);
                break;
            case TYPE_WAIT_FOR_HAVE_ANSWER:
                mListEmptyView.setContentText(R.string.you_not_has_answer_question);
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class AnswerAdapter extends ArrayAdapter<Answer> {
        private Context mContext;
        private int mAnswerType;

        public AnswerAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mine_wait_answer, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext, mAnswerType);
            return convertView;
        }

        public void setAnswerType(int answerType) {
            mAnswerType = answerType;
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

            public void bindDataWithView(Answer answer, Context context, int answerType) {
                if (answer == null) return;

//                mTime.setText(DateUtil.formatDefaultStyleTime(question.getCreateTime()));
//                if (question.isQuestionSolved()) {
//                    mContent.setSelected(true);
//                    mTitle.setEnabled(true);
//                    String priseCount = FinanceUtil.formatTenThousandNumber(question.getPriseCount());
//                    String replyCount = FinanceUtil.formatTenThousandNumber(question.getReplyCount());
//                    String awardCount = FinanceUtil.formatTenThousandNumber(question.getAwardCount());
//                    if (questionType == TYPE_QUESTION) {
//                        mContent.setText(context.getString(R.string.question_replay_content_award, priseCount, replyCount, awardCount));
//                    } else {
//                        mContent.setText(context.getString(R.string.question_replay_content, replyCount));
//                    }
//                } else {
//                    mContent.setSelected(false);
//                    mTitle.setEnabled(false);
//                    mContent.setText(context.getString(R.string.miss_is_answering));
//                }
//
//                mTitle.setText(question.getContent());
            }
        }
    }
}