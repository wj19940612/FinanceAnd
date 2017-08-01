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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.miss.MissMessage;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

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

    private MessageAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);
        initView();
        requestMessageData();
    }

    private void initView() {
        mMessageAdapter = new MessageAdapter(getActivity());
        mListView.setAdapter(mMessageAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mMessageAdapter);
        mTitle.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.unluckyText));
                for (int i = 0; i < mMessageAdapter.getCount(); i++) {
                    MissMessage missMessage = mMessageAdapter.getItem(i);
                    if (missMessage != null && !missMessage.isRead()) {
                        missMessage.setRead(true);
                    }
                }
                mMessageAdapter.notifyDataSetChanged();
                mTitle.setTitle(R.string.message_remind);
            }
        });
    }


    private void requestMessageData() {
        for (int i = 0; i < 10; i++) {
            MissMessage missMessage = new MissMessage();
            missMessage.setContent("我是级时候i试试");
            missMessage.setMessageType(i % 3);
            missMessage.setQuestion("哈哈哈哈啊哈哈哈哈哈");
            missMessage.setUserName("理想");
            mMessageAdapter.add(missMessage);
        }
        mMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        reset();
        requestMessageData();
    }

    private void reset() {
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    @Override
    public void onLoadMore() {
        requestMessageData();
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
                mQuestion.setText(item.getQuestion());
                Glide.with(context).load(R.drawable.ic_default_avatar).into(mAvatar);
                mUserName.setText(item.getUserName());
                mReplyContent.setText(item.getContent());
                switch (item.getMessageType()) {
                    case 0:
                        mMessageType.setText(context.getString(R.string.answer_your_question));
                        mMessageType.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_bounty), null, null, null);
                        break;
                    case 1:
                        mMessageType.setText(context.getString(R.string.comment_your_question));
                        break;
                    case 2:
                        mMessageType.setText(context.getString(R.string.reply_your_comment));
                        break;
                }
                mTime.setText(DateUtil.getFormatTime(System.currentTimeMillis()));
                if (item.isRead()) {
                    mRedDot.setVisibility(View.GONE);
                } else {
                    mRedDot.setVisibility(View.VISIBLE);
                }
                mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRedDot.setVisibility(View.GONE);
                        item.setRead(true);
                        mQuestion.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
                    }
                });
            }
        }
    }
}
