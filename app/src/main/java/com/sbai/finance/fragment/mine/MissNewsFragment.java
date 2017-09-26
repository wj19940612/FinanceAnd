package com.sbai.finance.fragment.mine;

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

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.NewsActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.miss.MissMessage;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 姐说消息
 */

public class MissNewsFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    ListEmptyView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private Unbinder mBind;
    private MessageAdapter mMessageAdapter;
    private Set<Integer> mSet;
    private int mNoReadCount;
    private NewsActivity.NoReadNewsCallback mNoReadNewsCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miss_news, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeaderView();
        initFooterView();
        initView();
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


    private void initView() {
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
                    //   requestMessageDetail(missMessage.getDataId());
                    if ("null".equalsIgnoreCase(missMessage.getMongoId())) {
                        Launcher.with(getActivity(), QuestionDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, missMessage.getDataId())
                                .execute();
                    } else {
                        Launcher.with(getActivity(), QuestionDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, missMessage.getDataId())
                                .putExtra(Launcher.EX_PAYLOAD_1, missMessage.getMongoId())
                                .execute();
                    }

                }
            }
        });
    }

    public void scrollToTop() {
        mListView.smoothScrollToPosition(0);
    }

    public void setNoReadNewsCallback(NewsActivity.NoReadNewsCallback noReadNewsCallback) {
        mNoReadNewsCallback = noReadNewsCallback;
    }

    private void requestMessageData() {
        Client.getQuestionMessageList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MissMessage>>, List<MissMessage>>() {
                    @Override
                    protected void onRespSuccessData(List<MissMessage> data) {
                        updateMessage(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fireFree();
    }

    private void updateMessage(List<MissMessage> data) {
        mNoReadCount = 0;
        mMessageAdapter.clear();
        for (MissMessage missMessage : data) {
            if (mSet.add(missMessage.getId())) {
                if (missMessage.isNoRead()) {
                    mNoReadCount++;
                }
                mMessageAdapter.add(missMessage);
            }
        }
        mMessageAdapter.notifyDataSetChanged();
        if (mNoReadNewsCallback != null) {
            mNoReadNewsCallback.noReadNews(mNoReadCount);
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

    public void requestBatchReadMessage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mMessageAdapter.getCount(); i++) {
            MissMessage missMessage = mMessageAdapter.getItem(i);
            if (missMessage != null && missMessage.isNoRead()) {
                sb.append(missMessage.getId()).append(",");
            }
        }
        if (sb.toString().length() == 0) return;
        sb.deleteCharAt(sb.length() - 1);
        Client.batchRead(sb.toString()).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            updateAllMessageStatus();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void updateMessageStatus(int msgId) {
        if (mNoReadCount > 0) {
            mNoReadCount--;
        }
        if (mNoReadNewsCallback != null) {
            mNoReadNewsCallback.noReadNews(mNoReadCount);
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

    private void updateAllMessageStatus() {
        mNoReadCount = 0;
        if (mMessageAdapter != null) {
            for (int i = 0; i < mMessageAdapter.getCount(); i++) {
                MissMessage missMessage = mMessageAdapter.getItem(i);
                if (missMessage != null && missMessage.isNoRead()) {
                    missMessage.setStatus(MissMessage.READ);
                }
            }
            mMessageAdapter.notifyDataSetChanged();
        }
        if (mNoReadNewsCallback != null) {
            mNoReadNewsCallback.noReadNews(mNoReadCount);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
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

    @Override
    public void onRefresh() {
        reset();
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
                if (item.getSourceUser() != null) {
                    if (item.getType() == MissMessage.TYPE_MISS_ANSWER) {
                        mUserName.setText(item.getSourceUser().getName());
                        GlideApp.with(context)
                                .load(item.getSourceUser().getPortrait())
                                .placeholder(R.drawable.ic_default_avatar)
                                .circleCrop()
                                .into(mAvatar);

                    } else {
                        mUserName.setText(item.getSourceUser().getUserName());
                        GlideApp.with(context)
                                .load(item.getSourceUser().getUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar)
                                .circleCrop()
                                .into(mAvatar);
                    }
                }
                if (item.getData() != null) {
                    mQuestion.setText(item.getData().getContent());
                }
                switch (item.getType()) {
                    case MissMessage.TYPE_MISS_ANSWER:
                        mMessageType.setText(context.getString(R.string.answer_your_question));
                        mMessageType.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_miss_message), null, null, null);
                        mReplyContent.setVisibility(View.GONE);
                        break;
                    case MissMessage.TYPE_COMMENT:
                        mReplyContent.setText(item.getMsg());
                        mMessageType.setText(context.getString(R.string.comment_your_question));
                        mMessageType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        mReplyContent.setVisibility(View.VISIBLE);
                        break;
                    case MissMessage.TYPE_REPLY:
                        mReplyContent.setText(item.getMsg());
                        mMessageType.setText(context.getString(R.string.reply_your_comment));
                        mMessageType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        mReplyContent.setVisibility(View.VISIBLE);
                        break;
                }
                mTime.setText(DateUtil.getMissFormatTime(item.getCreateTime()));
                if (item.isNoRead()) {
                    mRedDot.setVisibility(View.VISIBLE);
                } else {
                    mRedDot.setVisibility(View.GONE);
                }
            }
        }
    }
}
