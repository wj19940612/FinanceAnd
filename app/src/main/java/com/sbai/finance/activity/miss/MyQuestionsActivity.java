package com.sbai.finance.activity.miss;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.RewardMissDialogFragment;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.model.miss.RewardMoney;
import com.sbai.finance.model.missTalk.Prise;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的提问页面
 */
public class MyQuestionsActivity extends BaseActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    RelativeLayout mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.askQuestion)
    TextView mAskQuestion;

    private MyQuestionAdapter mMyQuestionAdapter;
    private Long mCreateTime;
    private int mPageSize = 20;
    private HashSet<Integer> mSet;
    private View mFootView;
    private RewardInfo mRewardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);
        ButterKnife.bind(this);
        initRewardInfo();
        mSet = new HashSet<>();
        mMyQuestionAdapter = new MyQuestionAdapter(this);
        mMyQuestionAdapter.setOnClickCallback(new MyQuestionAdapter.OnClickCallback() {
            @Override
            public void onRewardClick(Question item) {
                if (mRewardInfo != null) {
                    mRewardInfo.setId(item.getId());
                    mRewardInfo.setType(RewardInfo.TYPE_QUESTION);
                    mRewardInfo.setMoney(0);
                    mRewardInfo.setIndex(-1);
                    RewardMissDialogFragment.newInstance()
                            .show(getSupportFragmentManager());
                }
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mMyQuestionAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        requestMyQuestionList();
        initSwipeRefreshLayout();
    }

    private void initRewardInfo() {
        mRewardInfo = new RewardInfo();
        List<RewardMoney> list = new ArrayList<>();
        RewardMoney rewardMoney = new RewardMoney();
        rewardMoney.setMoney(10);
        list.add(rewardMoney);
        rewardMoney = new RewardMoney();
        rewardMoney.setMoney(100);
        list.add(rewardMoney);
        rewardMoney = new RewardMoney();
        rewardMoney.setMoney(1000);
        list.add(rewardMoney);
        mRewardInfo.setMoneyList(list);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mCreateTime = null;
                requestMyQuestionList();
            }
        });
    }

    public RewardInfo getRewardInfo() {
        return mRewardInfo;
    }


    private void requestMyQuestionList() {
        Client.getMyQuestionList(mCreateTime, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        updateMyQuestionList(questionList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateMyQuestionList(final List<Question> questionList) {
        if (questionList == null) {
            stopRefreshAnimation();
            return;
        }

        if (mFootView == null) {
            mFootView = View.inflate(getActivity(), R.layout.view_footer_load_more, null);
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;
                    mCreateTime = questionList.get(questionList.size() - 1).getCreateTime();
                    requestMyQuestionList();
                }
            });
            mListView.addFooterView(mFootView, null, true);
        }

        if (questionList.size() < mPageSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mMyQuestionAdapter != null) {
                mMyQuestionAdapter.clear();
                mMyQuestionAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }

        for (Question question : questionList) {
            if (mSet.add(question.getId())) {
                mMyQuestionAdapter.add(question);
            }
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    @OnClick(R.id.askQuestion)
    public void onViewClicked() {
        Launcher.with(getActivity(),SubmitQuestionActivity.class).execute();
    }

    static class MyQuestionAdapter extends ArrayAdapter<Question> {
        private Context mContext;
        private OnClickCallback mOnClickCallback;

        public void setOnClickCallback(OnClickCallback onClickCallback) {
            mOnClickCallback = onClickCallback;
        }

        interface OnClickCallback {
            void onRewardClick(Question item);
        }

        private MyQuestionAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_misstalk_answer, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.bindingData(mContext, getItem(position), mOnClickCallback);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.askTime)
            TextView mAskTime;
            @BindView(R.id.hotArea)
            RelativeLayout mHotArea;
            @BindView(R.id.question)
            TextView mQuestion;
            @BindView(R.id.missAvatar)
            ImageView mMissAvatar;
            @BindView(R.id.voice)
            TextView mVoice;
            @BindView(R.id.listenerNumber)
            TextView mListenerNumber;
            @BindView(R.id.loveNumber)
            TextView mLoveNumber;
            @BindView(R.id.commentNumber)
            TextView mCommentNumber;
            @BindView(R.id.ingotNumber)
            TextView mIngotNumber;
            @BindView(R.id.split)
            View mSplit;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(final Context context, final Question item, final OnClickCallback onClickCallback) {
                if (item == null) return;

                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);

                Glide.with(context).load(item.getCustomPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mMissAvatar);

                mName.setText(item.getUserName());
                mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
                mQuestion.setText(item.getQuestionContext());
                mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
                mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
                mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
                mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));
                mIngotNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickCallback.onRewardClick(item);
                    }
                });
                if (item.getIsPrise() == 0) {
                    mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
                } else {
                    mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
                }

                mLoveNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {
                            @Override
                            protected void onRespSuccessData(Prise prise) {
                                if (prise.getIsPrise() == 0) {
                                    mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
                                } else {
                                    mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
                                }
                                mLoveNumber.setText(StrFormatter.getFormatCount(prise.getPriseCount()));
                            }
                        }).fire();
                    }
                });
            }
        }
    }
}
