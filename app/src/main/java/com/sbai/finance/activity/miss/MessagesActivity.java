package com.sbai.finance.activity.miss;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.miss.MissMessage;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 姐说功能的消息提醒页面
 */
public class MessagesActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.title)
    TitleBar mTitle;
    @BindView(R.id.empty)
    TextView mEmpty;

    private MessageAdapter mMessageAdapter;
    private Set<Integer> mSet;
    private int mNoReadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);
        initHeaderView();
        initFooterView();
        initView();
        initTitle();
        requestMessageData();
    }

    private void initHeaderView() {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(1, getResources()));
        view.setLayoutParams(params);
        mListView.addHeaderView(view);
    }

    private void initFooterView() {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(1, getResources()));
        view.setLayoutParams(params);
        mListView.addFooterView(view);
    }

    private void initTitle() {
        mTitle.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mMessageAdapter.getCount(); i++) {
                    MissMessage missMessage = mMessageAdapter.getItem(i);
                    if (missMessage != null && missMessage.getStatus() == MissMessage.NO_READ) {
                        requestReadMessage(missMessage.getId());
                    }
                }
            }
        });
    }

    private void initView() {
        scrollToTop(mTitle, mListView);
        mSet = new HashSet<>();
        mMessageAdapter = new MessageAdapter(getActivity());
        mListView.setAdapter(mMessageAdapter);
        mListView.setEmptyView(mEmpty);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setLoadMoreEnable(false);
        mSwipeRefreshLayout.setAdapter(mListView, mMessageAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MissMessage missMessage = (MissMessage) parent.getItemAtPosition(position);
                TextView mQuestion = (TextView) view.findViewById(R.id.question);
                if (mQuestion != null) {
                    mQuestion.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                }
                if (missMessage != null) {
                    if (missMessage.isNoRead()) {
                        requestReadMessage(missMessage.getId());
                    }
                    requestMessageDetail(missMessage.getDataId());
                    // TODO: 2017-08-02 跳转到消息详情页
                }
            }
        });
    }

    private void requestMessageDetail(int dataId) {
        Client.getQuestionDetails(dataId).setTag(TAG)
                .setCallback(new Callback2D<Resp<Question>, Question>() {
                    @Override
                    protected void onRespSuccessData(Question data) {
                        Launcher.with(getActivity(), QuestionDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, data)
                                .execute();

                    }
                }).fireFree();
    }

    private void requestMessageData() {
        Client.getQuestionMessageList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MissMessage>>, List<MissMessage>>() {
                    @Override
                    protected void onRespSuccessData(List<MissMessage> data) {
                        updateMessage(data);
                    }
                }).fireFree();
    }

    private void updateMessage(List<MissMessage> data) {
        stopRefreshAnimation();
        mNoReadCount = 0;
        for (MissMessage missMessage : data) {
            if (mSet.add(missMessage.getId())) {
                if (missMessage.getStatus() == MissMessage.NO_READ) {
                    mNoReadCount++;
                }
                mMessageAdapter.add(missMessage);
            }
        }
        mMessageAdapter.notifyDataSetChanged();
        if (mNoReadCount == 0) {
            mTitle.setTitle(getString(R.string.message_remind));
            mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.unluckyText));
        } else {
            mTitle.setTitle(getString(R.string.message_remind_count, mNoReadCount));
            mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.blueAssist));
        }
    }

    private void requestReadMessage(final int msgId) {
        Client.readMessage(msgId).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            updateMessageStatus(msgId);
                        }
                    }
                }).fireFree();
    }

    private void updateMessageStatus(int msgId) {
        if (mNoReadCount > 0) {
            mNoReadCount--;
        }
        if (mNoReadCount == 0) {
            mTitle.setTitle(R.string.message_remind);
            mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.unluckyText));
        } else {
            mTitle.setTitle(getString(R.string.message_remind_count, mNoReadCount));
        }
        if (mMessageAdapter != null) {
            for (int i = 0; i < mMessageAdapter.getCount(); i++) {
                MissMessage missMessage = mMessageAdapter.getItem(i);
                if (missMessage != null && missMessage.getId() == msgId) {
                    missMessage.setStatus(MissMessage.READ);
                    mMessageAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        reset();
        requestMessageData();
    }

    private void reset() {
        mSet.clear();
    }

    @Override
    public void onLoadMore() {
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    static class MessageAdapter extends ArrayAdapter<MissMessage> {

        public MessageAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_messages_remind, null, true);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.content)
            LinearLayout mContent;
            @BindView(R.id.question)
            TextView mQuestion;
            @BindView(R.id.redDot)
            TextView mRedDot;
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.messageType)
            TextView mMessageType;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.replyContent)
            TextView mReplyContent;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(final MissMessage item, final Context context) {
                if (item.getSourceUser() != null) {
                    mUserName.setText(item.getSourceUser().getUserName());
                    Glide.with(context)
                            .load(item.getSourceUser().getUserPhone())
                            .transform(new GlideCircleTransform(context))
                            .into(mAvatar);
                }
                mQuestion.setText(item.getMsg());
                Glide.with(context).load(R.drawable.ic_default_avatar).into(mAvatar);
                if (item.getData() != null) {
                    mReplyContent.setText(item.getData().getQuestion());
                }
                switch (item.getType()) {
                    case MissMessage.TYPE_MISS_ANSWER:
                        mMessageType.setText(context.getString(R.string.answer_your_question));
                        if (item.getData() != null) {
                            mQuestion.setText(item.getData().getQuestion());
                        }
                        mMessageType.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_miss_message), null, null, null);
                        mReplyContent.setVisibility(View.GONE);
                        break;
                    case MissMessage.TYPE_COMMENT:
                        mMessageType.setText(context.getString(R.string.comment_your_question));
                        mReplyContent.setVisibility(View.VISIBLE);
                        break;
                    case MissMessage.TYPE_REPLY:
                        mMessageType.setText(context.getString(R.string.reply_your_comment));
                        mReplyContent.setVisibility(View.VISIBLE);
                        break;
                }
                mTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
                if (item.isNoRead()) {
                    mRedDot.setVisibility(View.VISIBLE);
                } else {
                    mRedDot.setVisibility(View.GONE);
                }
            }
        }
    }
}
