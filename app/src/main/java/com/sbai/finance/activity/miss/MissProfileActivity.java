package com.sbai.finance.activity.miss;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
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
import com.sbai.finance.model.missTalk.Attention;
import com.sbai.finance.model.missTalk.Miss;
import com.sbai.finance.model.missTalk.Prise;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 小姐姐详细资料页面
 */
public class MissProfileActivity extends BaseActivity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.askHerQuestion)
    LinearLayout mAskHerQuestion;
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.titleName)
    TextView mTitleName;
    @BindView(R.id.titleBar)
    RelativeLayout mTitleBar;

    private HerAnswerAdapter mHerAnswerAdapter;
    private float duration = 300.0f;
    private ArgbEvaluator evaluator = new ArgbEvaluator();
    private Long mCreateTime;
    private int mPageSize = 20;
    private HashSet<Integer> mSet;
    private int mCustomId;
    private View mFootView;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mVoice;
    private TextView mLoveNumber;
    private TextView mIntroduce;
    private LinearLayout mAttention;
    private LinearLayout mReward;
    private ImageView mAttentionStatus;
    private TextView mAttentionNumber;
    private TextView mRewardNumber;
    private RewardInfo mRewardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_profile);
        ButterKnife.bind(this);
        initData(getIntent());
        initRewardInfo();
        initHeaderView();
        mSet = new HashSet<>();
        mHerAnswerAdapter = new HerAnswerAdapter(this);
        //mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mHerAnswerAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        requestMissDetail();
        requestHerAnswerList();
        initSwipeRefreshLayout();
    }

    private void initData(Intent intent) {
        mCustomId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
    }

    private void initRewardInfo() {
        mRewardInfo = new RewardInfo();
        mRewardInfo.setId(mCustomId);
        mRewardInfo.setType(RewardInfo.TYPE_MISS);
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

    private void initHeaderView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_miss_profile, null);
        mAvatar = (ImageView) header.findViewById(R.id.avatar);
        mName = (TextView) header.findViewById(R.id.name);
        mVoice = (TextView) header.findViewById(R.id.voice);
        mLoveNumber = (TextView) header.findViewById(R.id.loveNumber);
        mIntroduce = (TextView) header.findViewById(R.id.introduce);
        mAttention = (LinearLayout) header.findViewById(R.id.attention);
        mReward = (LinearLayout) header.findViewById(R.id.reward);
        mAttentionStatus = (ImageView) header.findViewById(R.id.attentionStatus);
        mAttentionNumber = (TextView) header.findViewById(R.id.attentionNumber);
        mRewardNumber = (TextView) header.findViewById(R.id.rewardNumber);
        mReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardInfo != null) {
                    mRewardInfo.setMoney(0);
                    mRewardInfo.setIndex(-1);
                }
                RewardMissDialogFragment.newInstance()
                        .show(getSupportFragmentManager());
            }
        });
        mListView.addHeaderView(header);

    }

    private void requestMissDetail() {
        Client.getMissDetail(mCustomId).setTag(TAG)
                .setCallback(new Callback2D<Resp<Miss>, Miss>() {
                    @Override
                    protected void onRespSuccessData(Miss miss) {
                        updateMissDetail(miss);
                    }
                }).fire();
    }

    private void updateMissDetail(final Miss miss) {
        Glide.with(this).load(miss.getPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(this))
                .into(mAvatar);

        mName.setText(miss.getName());
        mVoice.setText(getString(R.string.voice_time, miss.getSoundTime()));
        mLoveNumber.setText(getString(R.string.love_people_number, miss.getTotalPrise()));
        mIntroduce.setText(miss.getBrifeingText());
        if (miss.isAttention() == 0) {
            mAttentionStatus.setImageResource(R.drawable.ic_not_attention);
        } else {
            mAttentionStatus.setImageResource(R.drawable.ic_attention);
        }
        mAttentionNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(miss.getTotalAttention())));
        mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(miss.getTotalAward())));

        mAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.attention(miss.getId()).setCallback(new Callback2D<Resp<Attention>, Attention>() {

                    @Override
                    protected void onRespSuccessData(Attention attention) {
                        if (attention.getIsAttention() == 0) {
                            mAttentionStatus.setImageResource(R.drawable.ic_not_attention);
                        } else {
                            mAttentionStatus.setImageResource(R.drawable.ic_attention);
                        }
                        mAttentionNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(attention.getAttentionCount())));
                    }
                }).fire();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Question item = (Question) parent.getItemAtPosition(position);
        if (item != null) {
            Launcher.with(this, QuestionDetailActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, item).execute();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public RewardInfo getRewardInfo() {
        return mRewardInfo;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mCreateTime = null;
                requestHerAnswerList();
            }
        });
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);

        int bgColor = 0X0affffff;
        float alpha = 0;
        if (getScrollY() < 0) {
            bgColor = 0X0aFFFFFF;
            alpha = 0;
        } else if (getScrollY() > 300) {
            bgColor = 0XFF55ADFF;
            alpha = 1;
        } else {
            bgColor = (int) evaluator.evaluate(getScrollY() / duration, 0X03aFFFFF, 0XFF55ADFF);
            alpha = getScrollY() / duration;
        }
        mTitleBar.setBackgroundColor(bgColor);
        mTitleName.setAlpha(alpha);
    }

    public int getScrollY() {
        View view = mListView.getChildAt(0);
        if (view == null) {
            return 0;
        }
        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = view.getTop();
        return -top + firstVisiblePosition * view.getHeight();
    }


    private void requestHerAnswerList() {
        Client.getHerAnswerList(mCustomId, mCreateTime, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        updateHerAnswerList(questionList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateHerAnswerList(final List<Question> questionList) {
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
                    requestHerAnswerList();
                }
            });
            mListView.addFooterView(mFootView, null, true);
        }

        if (questionList.size() < mPageSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mHerAnswerAdapter != null) {
                mHerAnswerAdapter.clear();
                mHerAnswerAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }

        for (Question question : questionList) {
            if (mSet.add(question.getId())) {
                mHerAnswerAdapter.add(question);
            }
        }
    }

    @OnClick(R.id.askHerQuestion)
    public void onViewClicked() {
        Launcher.with(getActivity(), SubmitQuestionActivity.class)
                .putExtra(Launcher.EX_PAYLOAD,mCustomId)
                .execute();
    }

    static class HerAnswerAdapter extends ArrayAdapter<Question> {

        private Context mContext;

        private HerAnswerAdapter(@NonNull Context context) {
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


            viewHolder.bindingData(mContext, getItem(position));
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

            public void bindingData(final Context context, final Question item) {
                if (item == null) return;

                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);

                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(context, MyQuestionsActivity.class).execute();
                    }
                });

                Glide.with(context).load(item.getCustomPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mMissAvatar);

                mName.setText(item.getUserName());
                mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
                mQuestion.setText(item.getQuestionContext());
                mVoice.setText(context.getString(R.string.voice_time, item.getSoundTime()));
                mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
                mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
                mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
                mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

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
