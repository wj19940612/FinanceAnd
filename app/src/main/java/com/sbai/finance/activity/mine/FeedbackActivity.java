package com.sbai.finance.activity.mine;

import android.content.Context;
import android.graphics.Color;
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

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.UploadFeedbackImageDialogFragment;
import com.sbai.finance.fragment.mine.PreviewFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.Feedback;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.EmojiFilter;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.GlideThumbTransform;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        initViews();
        requestFeedbackList(true);
    }

    private void initViews() {
        InputFilter[] filters = new InputFilter[2];
        filters[0] = new EmojiFilter();
        filters[1] = new InputFilter.LengthFilter(400);
        mCommentContent.setFilters(filters);
        mFeedbackList = new ArrayList<>();
        mFeedbackAdapter = new FeedbackAdapter(this, mFeedbackList);
        mListView.setAdapter(mFeedbackAdapter);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    //只有在登录的情况下 才请求记录
    private void requestFeedbackList(final boolean needScrollToLast) {
        Client.getFeedback(mPage, mPageSize)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Feedback>>, List<Feedback>>() {
                    @Override
                    protected void onRespSuccessData(List<Feedback> data) {
                        updateFeedbackList(data,needScrollToLast);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateFeedbackList(List<Feedback> data,boolean needScrollToLast) {
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
        mFeedbackAdapter.addFeedbackList(data);
        if (needScrollToLast){
            mListView.smoothScrollToPosition(mFeedbackAdapter.getCount() - 1);
        }
    }

    private void updateTitle(List<Feedback> data) {
        //已经设置过标题就不设置了
        if (!mTitleBar.getTitle().equals(getString(R.string.feed_back))) {
            return;
        }
        for (Feedback feedback : data) {
            if (!TextUtils.isEmpty(feedback.getReplyName())) {
                mTitleBar.setTitle(getString(R.string.feed_back) + "-" + feedback.getReplyName());
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
            mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
        }
    }

    @OnClick({R.id.send, R.id.addPic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                sendFeedbackText();
                break;
            case R.id.addPic:
                sendPicToCustomer();
                break;
        }
    }

    private void sendFeedbackText() {
        if (LocalUser.getUser().isLogin()) {
            String content = mCommentContent.getText().toString().trim();
            requestSendFeedback(content, CONTENT_TYPE_TEXT);
        } else {
            Launcher.with(this, LoginActivity.class).execute();
        }
    }

    private void sendPicToCustomer() {
        UploadFeedbackImageDialogFragment.newInstance()
                .setOnDismissListener(new UploadFeedbackImageDialogFragment.OnDismissListener() {
                    @Override
                    public void onGetImagePath(String path) {
                        requestSendFeedbackImage(path);
                    }
                })
                .show(getSupportFragmentManager());
    }

    private void requestSendFeedbackImage(final String path) {
        String content = ImageUtils.compressImageToBase64(path);
        int contentType = CONTENT_TYPE_PICTURE;
        requestSendFeedback(content, contentType);
    }

    private void requestSendFeedback(final String content, final int contentType) {
        Client.sendFeedback(content, contentType)
                .setTag(TAG)
                .setIndeterminate(FeedbackActivity.this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        // TODO: 2017/5/10 刷新界面
                        refreshChatList(content, contentType);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }
                })
                .fireSync();
    }

    //刷新列表
    private void refreshChatList(final String content, final int contentType) {
        //请求最新的服务器数据  并取第一条
        Client.getFeedback(0, mPageSize)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Feedback>>, List<Feedback>>() {
                    @Override
                    protected void onRespSuccessData(List<Feedback> data) {
                        updateTheLastMessage(data, content, contentType);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateTheLastMessage(List<Feedback> data, String content, int contentType) {
        if (data == null) {
            return;
        }
        Feedback feedback = data.get(0);
        mFeedbackAdapter.addFeedbackItem(feedback);
        mListView.setSelection(mFeedbackAdapter.getCount() - 1);
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
            if (DateUtil.isToday(nextTime, preTime)) {
                return false;
            }
            return true;
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
                    mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateDate()));
                } else {
                    mTimeLayout.setVisibility(View.GONE);
                }
                mTimestamp.setText(DateUtil.format(feedback.getCreateDate(), FORMAT_HOUR_MINUTE));

                mWrapper.removeAllViews();
                TextView textview = null;
                ImageView imageview = null;
                //判断是否图片
                if (feedback.getContentType() == CONTENT_TYPE_TEXT) {
                    //create textview and add
                    textview = createTextview(context);
                    mWrapper.addView(textview);
                    textview.setText(feedback.getContent());
                } else {
                    //create imageview and add
                    imageview = createImageview(context);
                    mWrapper.addView(imageview);
                    Glide.with(context).load(feedback.getContent())
                            .bitmapTransform(new GlideThumbTransform(context))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageview);
                }
                Glide.with(context).load(feedback.getUserPortrait())
                        .bitmapTransform(new GlideCircleTransform(context))
                        .placeholder(R.drawable.ic_avatar_feedback)
                        .into(mHeadImage);
                if (imageview != null) {
                    imageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (feedback.getContentType() == CONTENT_TYPE_PICTURE) {
                                PreviewFragment.newInstance(feedback.getContent())
                                        .show(((FeedbackActivity) context).getSupportFragmentManager());
                            }
                        }
                    });
                }

                mHeadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(context, UserDataActivity.class)
                                .putExtra(Launcher.USER_ID, LocalUser.getUser().getUserInfo().getId()).execute();
                    }
                });

            }

            private TextView createTextview(Context context) {
                TextView tv = new TextView(context);
                tv.setTextColor(Color.WHITE);
                int spacing = (int) Display.dp2Px(5.0f, context.getResources());
                tv.setLineSpacing(spacing, 1.2f);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int margin = (int) Display.dp2Px(10.0f, context.getResources());
                params.setMargins(0, 0, margin, 0);
                tv.setLayoutParams(params);
                return tv;
            }

            private ImageView createImageview(Context context) {
                ImageView image = new ImageView(context);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                int margin = (int) Display.dp2Px(5.0f, context.getResources());
                params.setMargins(0, 0, margin, 0);
                image.setLayoutParams(params);
                return image;
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
                    mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateDate()));
                } else {
                    mTimeLayout.setVisibility(View.GONE);
                }
                mTimestamp.setText(DateUtil.format(feedback.getCreateDate(), FORMAT_HOUR_MINUTE));
                mText.setText(feedback.getContent());
                Glide.with(context).load(R.drawable.ic_feedback_service)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mHeadImage);
            }
        }
    }

}
