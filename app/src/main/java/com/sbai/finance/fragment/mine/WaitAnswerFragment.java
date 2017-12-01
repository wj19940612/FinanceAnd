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
import com.sbai.finance.activity.mine.WaitForMeAnswerActivity;
import com.sbai.finance.activity.miss.MissAudioReplyActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.Answer;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.activity.mine.WaitForMeAnswerActivity.WAIT_ME_ANSWER;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class WaitAnswerFragment extends BaseFragment {

    private static final String ANSWER_TYPE = "answer_type";

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
        bundle.putInt(ANSWER_TYPE, type);
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
        mAnswerAdapter.setAnswerType(mAnswerType);
        mListView.setAdapter(mAnswerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Answer answer = (Answer) parent.getAdapter().getItem(position);
                requestUpdateReadStatus(answer.getId());

                Launcher.with(getActivity(), MissAudioReplyActivity.class)
                        .putExtra(ExtraKeys.QUESTION_ID,answer.getId())
                        .execute();

//                //TODO 跳转回答录音界面
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
        mSwipeRefreshLayout.setLoadMoreEnable(false);
        requestWaitForMeAnswer();
    }

    private void requestWaitForMeAnswer() {
        Client.waitMeAnswer(mAnswerType)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Answer>>, List<Answer>>() {
                    @Override
                    protected void onRespSuccessData(List<Answer> data) {
                        updateAnswerList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateAnswerList(List<Answer> data) {
        mAnswerAdapter.clear();
        mAnswerAdapter.addAll(data);
    }

    private void requestUpdateReadStatus(int id){
        Client.updateAnswerReadStatus(id).setTag(TAG).fire();
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
            case WAIT_ME_ANSWER:
                mListEmptyView.setContentText(R.string.you_not_has_answer);
                mListEmptyView.setGoingBtnGone();
                break;
            case WaitForMeAnswerActivity.HAVE_ANSWER:
                mListEmptyView.setContentText(R.string.you_not_has_answer_question);
                mListEmptyView.setGoingBtnGone();
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
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.iconView)
            View mIconView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(Answer answer, Context context, int answerType) {
                if (answer == null) return;
                mTime.setText(DateUtil.formatDefaultStyleTime(answer.getCreateTime()));
                mTitle.setText(answer.getQuestionContext());
                if (answer.getReadStatus() == 0 && answerType == WAIT_ME_ANSWER) {
                    mIconView.setVisibility(View.VISIBLE);
                } else {
                    mIconView.setVisibility(View.GONE);
                }
            }
        }
    }
}
