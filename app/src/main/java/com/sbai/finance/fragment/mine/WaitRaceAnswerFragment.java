package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.WaitForMeAnswerActivity;
import com.sbai.finance.activity.miss.MissAudioReplyActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.Answer;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;
import com.sbai.httplib.ApiError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class WaitRaceAnswerFragment extends BaseFragment {
    public static final int QUESTION_RUSH_TO_ANSWER_FAIL = 4710;

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

    public static WaitRaceAnswerFragment newInstance() {
        WaitRaceAnswerFragment fragment = new WaitRaceAnswerFragment();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            refreshData();
        }
    }

    private void initView() {
        initListEmptyView();
        mListView.setEmptyView(mListEmptyView);
        mListView.setDivider(null);
        mRaceAnswerAdapter = new RaceAnswerAdapter(getActivity());
        mRaceAnswerAdapter.setRaceClickListener(new RaceAnswerAdapter.RaceClickListener() {
            @Override
            public void raceClick(Answer item) {
                requestrushToAnswer(item);
            }
        });
        mListView.setAdapter(mRaceAnswerAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

    }

    private void refreshData() {
        mPage = 0;
        mSwipeRefreshLayout.setLoadMoreEnable(false);
        requestWaitForMeAnswer(true);
    }

    private void requestWaitForMeAnswer(final boolean isRefreshing) {
        Client.waitMeAnswer(WaitForMeAnswerActivity.WAIT_RACE_ANSWER)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Answer>>, List<Answer>>() {
                    @Override
                    protected void onRespSuccessData(List<Answer> data) {
                        updateRaceList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateRaceList(List<Answer> data) {
        mRaceAnswerAdapter.clear();
        mRaceAnswerAdapter.addAll(data);
    }


    private void requestrushToAnswer(final Answer item) {
        Client.rushToAnswer(item.getId()).setTag(TAG).setCallback(new Callback<Resp<Object>>() {
            @Override
            protected void onRespSuccess(Resp<Object> resp) {
                updateRaceResult(item);
            }

            @Override
            protected void onRespFailure(Resp failedResp) {
                super.onRespFailure(failedResp);
                if (failedResp.getCode() == QUESTION_RUSH_TO_ANSWER_FAIL) {
                    refreshData();
                }
            }
        }).fire();
    }

    private void updateRaceResult(Answer item) {
        if (item != null) {
            Launcher.with(getActivity(), MissAudioReplyActivity.class)
                    .putExtra(ExtraKeys.QUESTION_ID, item.getId())
                    .execute();
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

    private void initListEmptyView() {
        mListEmptyView.setContentText(R.string.not_has_answer);
        mListEmptyView.setGoingBtnGone();
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
            public void raceClick(Answer item);
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mine_race_answer, parent, false);
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
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.btnRaceAnswer)
            TextView mBtnRaceAnswer;
            @BindView(R.id.iconView)
            View mIconView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(final Answer item, Context context, final RaceClickListener raceClickListener) {
                mBtnRaceAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (raceClickListener != null) {
                            raceClickListener.raceClick(item);
                        }
                    }
                });
                mTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateTime()));
                mTitle.setText(item.getQuestionContext());
                if (item.getReadStatus() == 0) {
                    mIconView.setVisibility(View.VISIBLE);
                } else {
                    mIconView.setVisibility(View.GONE);
                }
            }
        }
    }
}
