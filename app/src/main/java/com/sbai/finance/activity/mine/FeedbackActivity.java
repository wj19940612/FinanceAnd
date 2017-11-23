package com.sbai.finance.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.PreviewDialogFragment;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.Feedback;
import com.sbai.finance.model.system.ServiceConnectWay;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.image.ImageUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.transform.ThumbTransform;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.ApiError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.model.mine.Feedback.CONTENT_TYPE_PICTURE;
import static com.sbai.finance.model.mine.Feedback.CONTENT_TYPE_TEXT;
import static com.sbai.finance.utils.DateUtil.FORMAT_HOUR_MINUTE;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class FeedbackActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.commentContent)
    EditText mCommentContent;
    @BindView(R.id.addPic)
    ImageButton mAddPic;
    @BindView(R.id.send)
    TextView mSend;


    private int mPage = 0;
    private int mPageSize = 15;
    private boolean mLoadMoreEnable = true;

    private List<Feedback> mFeedbackList;
    private FeedbackAdapter mFeedbackAdapter;
    private int mTrainId;

    private boolean firstLoadData = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        initData(getIntent());
        initViews();

        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
        requestFeedbackList(true);
    }

    private void initData(Intent intent) {
        mTrainId = intent.getIntExtra(ExtraKeys.TRAINING, -1);
    }

    private void initViews() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(400);
        mCommentContent.setFilters(filters);
        mFeedbackList = new ArrayList<>();
        mFeedbackAdapter = new FeedbackAdapter(this, mFeedbackList);
        mListView.setAdapter(mFeedbackAdapter);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    //只有在登录的情况下 才请求记录
    private void requestFeedbackList(final boolean needScrollToLast) {
        if (mTrainId == -1) {
            Client.getFeedback(mPage, mPageSize)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<List<Feedback>>, List<Feedback>>() {
                        @Override
                        protected void onRespSuccessData(List<Feedback> data) {
                            updateFeedbackList(data, needScrollToLast);
                        }

                        @Override
                        public void onFailure(ApiError apiError) {
                            super.onFailure(apiError);
                            stopRefreshAnimation();
                        }
                    })
                    .fire();
        } else {
            Client.getTrainFeedbackList(mTrainId, mPage, mPageSize)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<List<Feedback>>, List<Feedback>>() {
                        @Override
                        protected void onRespSuccessData(List<Feedback> data) {
                            updateFeedbackList(data, needScrollToLast);
                        }

                        @Override
                        public void onFailure(ApiError apiError) {
                            super.onFailure(apiError);
                            stopRefreshAnimation();
                        }
                    })
                    .fire();
        }
    }

    private void updateFeedbackList(List<Feedback> data, boolean needScrollToLast) {
        if (data == null) {
            stopRefreshAnimation();
            return;
        }

        stopRefreshAnimation();
        if (data.size() < mPageSize) {
            mLoadMoreEnable = false;
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mPage++;
        }
        updateTitle(data);
        if (firstLoadData) {
            //自己创建一个客服对话
            createServiceTalk(data);
            firstLoadData = false;
        }
        mFeedbackAdapter.addFeedbackList(data);
        if (needScrollToLast) {
            mListView.setSelection(View.FOCUS_DOWN);
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.setSelection(View.FOCUS_DOWN);
                }
            }, 240);
        }
    }

    private void createServiceTalk(List<Feedback> data) {
        Feedback feedback = new Feedback();
        feedback.setType(1);
        String weChatConnect = "lemi202";
        ServiceConnectWay serviceConnectWay = Preference.get().getServiceConnectWay();

        if (serviceConnectWay != null && !TextUtils.isEmpty(serviceConnectWay.getWeixin())) {
            weChatConnect = serviceConnectWay.getWeixin();
        }
        feedback.setContent(getString(R.string.send_message_connect, weChatConnect));
        feedback.setCreateDate(System.currentTimeMillis());
        data.add(0, feedback);
    }

    private void listViewScrollBottom() {
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);

//        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
//            mListView.setStackFromBottom(false);
    }

    private void updateTitle(List<Feedback> data) {
        //已经设置过标题就不设置了
        if (!mTitleBar.getTitle().equals(getString(R.string.feedback))) {
            return;
        }
        for (Feedback feedback : data) {
            if (!TextUtils.isEmpty(feedback.getReplyName())) {
                mTitleBar.setTitle(getString(R.string.feedback) + "-" + feedback.getReplyName());
                break;
            }
        }
    }

    @Override
    public void onRefresh() {
        requestFeedbackList(false);
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        if (mLoadMoreEnable) {
//            mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            if (firstVisibleItem == 0 && topRowVerticalPosition >= 0) {
                mSwipeRefreshLayout.setEnabled(true);
            } else {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }

        }
    }

    @OnClick({R.id.send, R.id.addPic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (LocalUser.getUser().isLogin()) {

                    if (mFeedbackAdapter.getCount() > 6) {
                        listViewScrollBottom();
                    }
                    sendFeedbackText();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.addPic:
                if(LocalUser.getUser().isLogin()){
                    if (mFeedbackAdapter.getCount() > 6) {
                        listViewScrollBottom();
                    }
                    sendPicToCustomer();
                }else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void sendFeedbackText() {
        if (LocalUser.getUser().isLogin() || mTrainId != -1) {
            String content = mCommentContent.getText().toString().trim();
            requestSendFeedback(content, CONTENT_TYPE_TEXT);
        } else {
            Launcher.with(this, LoginActivity.class).execute();
        }
    }

    private void sendPicToCustomer() {
        UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.IMAGE_TYPE_OPEN_CUSTOM_GALLERY)
                .setOnImagePathListener(new UploadUserImageDialogFragment.OnImagePathListener() {
                    @Override
                    public void onImagePath(int index, String imagePath) {
                        requestSendFeedbackImage(imagePath);
                    }
                }).show(getSupportFragmentManager());
    }

    private void requestSendFeedbackImage(final String path) {
        String content = ImageUtils.compressImageToBase64(path, getActivity());
        int contentType = CONTENT_TYPE_PICTURE;
        requestSendFeedback(content, contentType);
    }

    private void requestSendFeedback(final String content, final int contentType) {
        if (mTrainId == -1) {
            Client.sendFeedback(content, contentType)
                    .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setTag(TAG)
                    .setIndeterminate(FeedbackActivity.this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            refreshChatList(content, contentType);
                        }

                        @Override
                        public void onFailure(ApiError apiError) {
                            super.onFailure(apiError);
                        }
                    })
                    .fireFree();
        } else {
            if (contentType == CONTENT_TYPE_PICTURE) {
                Client.uploadPicture(content).setTag(TAG).setIndeterminate(this)
                        .setCallback(new Callback2D<Resp<List<String>>, List<String>>() {
                            @Override
                            protected void onRespSuccessData(List<String> data) {
                                if (!data.isEmpty()) {
                                    requestSendTrainFeedback(data.get(0), contentType);
                                }
                            }
                        }).fireFree();
            } else {
                requestSendTrainFeedback(content, contentType);
            }

        }
    }

    private void requestSendTrainFeedback(final String content, final int contentType) {
        Client.trainFeedback(mTrainId, content, contentType)
                .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .setTag(TAG)
                .setIndeterminate(FeedbackActivity.this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        refreshChatList(content, contentType);
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                    }
                })
                .fireFree();
    }

    //刷新列表
    private void refreshChatList(final String content, final int contentType) {
        //请求最新的服务器数据  并取第一条
        if (mTrainId == -1) {
            Client.getFeedback(0, mPageSize)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<List<Feedback>>, List<Feedback>>() {
                        @Override
                        protected void onRespSuccessData(List<Feedback> data) {
                            updateTheLastMessage(data, content, contentType);
                        }

                        @Override
                        public void onFailure(ApiError apiError) {
                            super.onFailure(apiError);
                            stopRefreshAnimation();
                        }
                    })
                    .fire();
        } else {
            Client.getTrainFeedbackList(mTrainId, 0, mPageSize)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<List<Feedback>>, List<Feedback>>() {
                        @Override
                        protected void onRespSuccessData(List<Feedback> data) {
                            updateTheLastMessage(data, content, contentType);
                        }

                        @Override
                        public void onFailure(ApiError apiError) {
                            super.onFailure(apiError);
                            stopRefreshAnimation();
                        }
                    })
                    .fire();
        }
    }

    private void updateTheLastMessage(List<Feedback> data, String content, int contentType) {
        if (data == null || data.isEmpty()) {
            return;
        }
        Feedback feedback = data.get(0);
        mFeedbackAdapter.addFeedbackItem(feedback);

        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mFeedbackAdapter.getCount() - 1);
            }
        }, 240);
        if (contentType == CONTENT_TYPE_TEXT) {
            feedback.setContent(content);
            mCommentContent.setText("");
        }
    }


    static class FeedbackAdapter extends BaseAdapter {

        public static final int TYPE_USER = 0;
        public static final int TYPE_CUSTOMER = 1;

        private Context mContext;
        private List<Feedback> mFeedbackList;

        public FeedbackAdapter(Context context, List<Feedback> list) {
            mContext = context;
            mFeedbackList = list;
        }

        public void addFeedbackList(List<Feedback> list) {
            int length = list.size();
            //如果原来已经有数据 则倒序插入
            if (mFeedbackList.size() > 0) {
                for (int i = 0; i < length; i++) {
                    mFeedbackList.add(0, list.get(i));
                }
            } else {
                Collections.reverse(list);
                mFeedbackList.addAll(list);
            }
            notifyDataSetChanged();
        }

        public void addFeedbackItem(Feedback feedback) {
            mFeedbackList.add(feedback);
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return mFeedbackList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFeedbackList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (mFeedbackList.get(position).getType() == TYPE_USER) {
                return TYPE_USER;
            } else {
                return TYPE_CUSTOMER;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        private boolean needTitle(int position) {
            if (position == 0) {
                return true;
            }

            if (position < 0) {
                return false;
            }

            Feedback pre = mFeedbackList.get(position - 1);
            Feedback next = mFeedbackList.get(position);
            //判断两个时间在不在一天内  不是就要显示标题
            long preTime = pre.getCreateDate();
            long nextTime = next.getCreateDate();
            return !DateUtil.isToday(nextTime, preTime);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserViewHolder userViewHolder = null;
            CustomerViewHolder customerViewHolder = null;
            switch (getItemViewType(position)) {
                case TYPE_USER:
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.row_feedback_user, null);
                        userViewHolder = new UserViewHolder(convertView);
                        convertView.setTag(userViewHolder);
                    } else {
                        userViewHolder = (UserViewHolder) convertView.getTag();
                    }
                    userViewHolder.bindingData(mFeedbackList.get(position), needTitle(position), mContext);
                    break;
                case TYPE_CUSTOMER:
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.row_feedback_customer, null);
                        customerViewHolder = new CustomerViewHolder(convertView);
                        convertView.setTag(customerViewHolder);
                    } else {
                        customerViewHolder = (CustomerViewHolder) convertView.getTag();
                    }
                    customerViewHolder.bindingData(mFeedbackList.get(position), needTitle(position), mContext);
                    break;
            }
            return convertView;
        }

        static class UserViewHolder {
            @BindView(R.id.endLineTime)
            TextView mEndLineTime;
            @BindView(R.id.timeLayout)
            RelativeLayout mTimeLayout;
            @BindView(R.id.contentWrapper)
            RelativeLayout mWrapper;
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.image)
            ImageView mImage;
            @BindView(R.id.text)
            TextView mText;
            @BindView(R.id.headImage)
            ImageView mHeadImage;

            public UserViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(final Feedback feedback, boolean needTitle, final Context context) {
                if (needTitle) {
                    mTimeLayout.setVisibility(View.VISIBLE);
                    if (feedback.getCreateDate() > 0) {
                        mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateDate()));
                    } else {
                        long time = System.currentTimeMillis();
                        if (!TextUtils.isEmpty(feedback.getCreateTime())) {
                            time = DateUtil.convertString2Long(feedback.getCreateTime(), DateUtil.DEFAULT_FORMAT);
                        }
                        mEndLineTime.setText(DateUtil.getFeedbackFormatTime(time));
                    }
                } else {
                    mTimeLayout.setVisibility(View.GONE);
                }
                if (feedback.getCreateDate() > 0) {
                    mTimestamp.setText(DateUtil.format(feedback.getCreateDate(), FORMAT_HOUR_MINUTE));
                } else {
                    long time = System.currentTimeMillis();
                    if (!TextUtils.isEmpty(feedback.getCreateTime())) {
                        time = DateUtil.convertString2Long(feedback.getCreateTime(), DateUtil.DEFAULT_FORMAT);
                    }
                    mTimestamp.setText(DateUtil.format(time, FORMAT_HOUR_MINUTE));
                }

                //判断是否图片
                if (feedback.getContentType() == CONTENT_TYPE_TEXT) {
                    mText.setVisibility(View.VISIBLE);
                    mImage.setVisibility(View.GONE);
                    mText.setText(feedback.getContent());
                } else {
                    mText.setVisibility(View.GONE);
                    mImage.setVisibility(View.VISIBLE);
                    GlideApp.with(context).load(feedback.getContent())
                            .transform(new ThumbTransform(context))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mImage);
                }
                if (!TextUtils.isEmpty(feedback.getUserPortrait())) {
                    GlideApp.with(context).load(feedback.getUserPortrait())
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar_feedback)
                            .into(mHeadImage);
                } else {
                    GlideApp.with(context).load(feedback.getPortrait())
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar_feedback)
                            .into(mHeadImage);
                }
                if (mImage != null) {
                    mImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (feedback.getContentType() == CONTENT_TYPE_PICTURE) {
                                PreviewDialogFragment.newInstance(feedback.getContent())
                                        .show(((FeedbackActivity) context).getSupportFragmentManager());
                            }
                        }
                    });
                }
            }
        }

        static class CustomerViewHolder {
            @BindView(R.id.endLineTime)
            TextView mEndLineTime;
            @BindView(R.id.timeLayout)
            RelativeLayout mTimeLayout;
            @BindView(R.id.headImage)
            ImageView mHeadImage;
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.text)
            TextView mText;

            public CustomerViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Feedback feedback, boolean needTitle, Context context) {
                if (needTitle) {
                    mTimeLayout.setVisibility(View.VISIBLE);
                    //不是很清楚时间戳为何要用两个。。。。。。。。。。。。。。。。。。。。。。。。。。。。
                    if (feedback.getCreateDate() > 0) {
                        mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateDate()));
                    } else {
                        long time = System.currentTimeMillis();
                        if (!TextUtils.isEmpty(feedback.getCreateTime())) {
                            time = DateUtil.convertString2Long(feedback.getCreateTime(), DateUtil.DEFAULT_FORMAT);
                        }
                        mEndLineTime.setText(DateUtil.getFeedbackFormatTime(time));
                    }
                } else {
                    mTimeLayout.setVisibility(View.GONE);
                }
                if (feedback.getCreateDate() > 0) {
                    mTimestamp.setText(DateUtil.format(feedback.getCreateDate(), FORMAT_HOUR_MINUTE));
                } else {
                    long time = System.currentTimeMillis();
                    if (!TextUtils.isEmpty(feedback.getCreateTime())) {
                        time = DateUtil.convertString2Long(feedback.getCreateTime(), DateUtil.DEFAULT_FORMAT);
                    }
                    mTimestamp.setText(DateUtil.format(time, FORMAT_HOUR_MINUTE));
                }
                mText.setText(feedback.getContent());
                GlideApp.with(context).load(R.drawable.ic_feedback_service)
                        .circleCrop()
                        .into(mHeadImage);
            }
        }
    }

}
