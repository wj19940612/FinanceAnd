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

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.Answer;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class WaitRaceAnswerFragment extends BaseFragment {

    private Unbinder mBind;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(R.id.listEmptyView)
    ListEmptyView mListEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private RaceAnswerAdapter mRaceAnswerAdapter;
    private int mPage;
    private int mAnswerType = 1;

    public static WaitAnswerFragment newInstance() {
        WaitAnswerFragment fragment = new WaitAnswerFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wait_race_answer, container, false);
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
        mRaceAnswerAdapter = new RaceAnswerAdapter(getActivity());
        mRaceAnswerAdapter.setRaceClickListener(new RaceAnswerAdapter.RaceClickListener() {
            @Override
            public void raceClick() {

            }
        });
        mListView.setAdapter(mRaceAnswerAdapter);
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
        //TODO 待抢答请求
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
        mListEmptyView.setContentText(R.string.not_has_answer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class RaceAnswerAdapter extends ArrayAdapter<Answer> {
        private RaceClickListener mRaceClickListener;

        private void setRaceClickListener(RaceClickListener raceClickListener) {
            mRaceClickListener = raceClickListener;
        }

        public interface RaceClickListener {
            public void raceClick();
        }

        private Context mContext;

        public RaceAnswerAdapter(@NonNull Context context) {
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
            viewHolder.bindDataWithView(getItem(position), mContext, mRaceClickListener);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.title)
            AppCompatTextView mTitle;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.btnRaceAnswer)
            TextView mBtnRaceAnswer;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(Answer item, Context context, final RaceClickListener raceClickListener) {
                mBtnRaceAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (raceClickListener != null) {
                            raceClickListener.raceClick();
                        }
                    }
                });
            }
        }
    }
}
