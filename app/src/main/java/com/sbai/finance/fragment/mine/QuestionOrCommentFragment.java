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
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.NumberFormatUtils;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class QuestionOrCommentFragment extends BaseFragment {

    private static final String QUESTION_TYPE = "question_type";

    //我的回答的提问
    public static final int TYPE_QUESTION = 0;
    //评论
    public static final int TYPE_COMMENT = 1;

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listEmptyView)
    ListEmptyView mListEmptyView;

    private int mQuestionType;
    private Unbinder mBind;
    private MineQuestionAndAnswerAdapter mMineQuestionAndAnswerAdapter;
    private HashSet<Integer> mSet;
    private int mPage;


    public QuestionOrCommentFragment() {
    }

    public static QuestionOrCommentFragment newInstance(int type) {
        QuestionOrCommentFragment fragment = new QuestionOrCommentFragment();
        Bundle args = new Bundle();
        args.putInt(QUESTION_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionType = getArguments().getInt(QUESTION_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_or_comment, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mSet = new HashSet<>();
    }

    private void initView() {
        initListEmptyView();
        mListView.setEmptyView(mListEmptyView);
        mListView.setDivider(null);
        mMineQuestionAndAnswerAdapter = new MineQuestionAndAnswerAdapter(getActivity());
        mListView.setAdapter(mMineQuestionAndAnswerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                mSet.clear();
                requestMineQuestionOrComment(true);
            }
        });
        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestMineQuestionOrComment(false);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question question = (Question) parent.getAdapter().getItem(position);
                if (question != null) {
                    Launcher.with(getActivity(), QuestionDetailActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, question.getId())
                            .execute();
                }
            }
        });
    }

    private void initListEmptyView() {

        switch (mQuestionType) {
            case TYPE_QUESTION:
                mListEmptyView.setGoingText(R.string.go_to_question);
                mListEmptyView.setContentText(R.string.you_not_has_question);
                mListEmptyView.setHintText(R.string.this_will_show_your_question);
                mListEmptyView.setOnGoingViewClickListener(new ListEmptyView.OnGoingViewClickListener() {
                    @Override
                    public void onGoingViewClick() {

                    }
                });
                break;
            case TYPE_COMMENT:
                mListEmptyView.setGoingText(R.string.go_to_comment);
                mListEmptyView.setContentText(R.string.you_not_has_comment);
                mListEmptyView.setHintText(R.string.this_will_show_comment_question);
                mListEmptyView.setOnGoingViewClickListener(new ListEmptyView.OnGoingViewClickListener() {
                    @Override
                    public void onGoingViewClick() {
                        Launcher.with(getActivity(), MainActivity.class).putExtra(ExtraKeys.MAIN_PAGE_CURRENTITEM, 1).execute();
                        getActivity().finish();
                    }
                });
                break;
        }
    }


    private void requestMineQuestionOrComment(final boolean isRefreshing) {
        // TODO: 2017/9/25 测试数据 
        ArrayList<Question> questions = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Question question = new Question();
            question.setCreateTime(System.currentTimeMillis() + i);
            if (i % 2 == 0) {
                question.setSolve(0);
            } else {
                question.setSolve(i);
            }
            question.setId((int) (System.currentTimeMillis() + i));
            question.setAwardCount(i);
            question.setReplyCount(i);
            question.setPriseCount(i);
            question.setQuestionContext("测试 " + i + "   " + " hahjkdsahkj");
            questions.add(question);

        }
        updateQuestionList(questions, isRefreshing);


        Client.requestMineQuestionOrComment()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> data) {
                        updateQuestionList(data, isRefreshing);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateQuestionList(List<Question> data, boolean isRefreshing) {
        if (data == null) return;
        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (isRefreshing) mMineQuestionAndAnswerAdapter.clear();

        for (Question result : data) {
            if (mSet.add(result.getId())) {
                mMineQuestionAndAnswerAdapter.add(result);
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
        mBind.unbind();
    }

    static class MineQuestionAndAnswerAdapter extends ArrayAdapter<Question> {

        private Context mContext;

        public MineQuestionAndAnswerAdapter(@NonNull Context context) {
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

            public void bindDataWithView(Question question, Context context) {
                if (question == null) return;

                mTime.setText(DateUtil.formatQuestionStyleTime(question.getCreateTime()));
                if (question.isQuestionSolved()) {
                    mContent.setSelected(true);
                    mTitle.setEnabled(true);
                    String priseCount = NumberFormatUtils.formatTenThousandNumber(question.getPriseCount());
                    String replyCount = NumberFormatUtils.formatTenThousandNumber(question.getReplyCount());
                    String awardCount = NumberFormatUtils.formatTenThousandNumber(question.getAwardCount());
                    mContent.setText(context.getString(R.string.question_replay_content, priseCount, replyCount, awardCount));
                } else {
                    mContent.setSelected(false);
                    mTitle.setEnabled(false);
                    mContent.setText(context.getString(R.string.miss_is_answering));
                }

                mTitle.setText(question.getQuestionContext());
            }
        }
    }
}
