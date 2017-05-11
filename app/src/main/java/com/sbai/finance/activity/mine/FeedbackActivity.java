package com.sbai.finance.activity.mine;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.UploadFeedbackImageDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.Feedback;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.model.mine.Feedback.CONTENT_TYPE_PICTURE;
import static com.sbai.finance.model.mine.Feedback.CONTENT_TYPE_TEXT;

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

    private List<Feedback> mFeedbackList;
    private FeedbackAdapter mFeedbackAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        initViews();
        requestFeedbackList();
    }

    private void initViews() {
        mFeedbackList = new ArrayList<>();
        mFeedbackAdapter = new FeedbackAdapter(this, mFeedbackList);
        mListView.setAdapter(mFeedbackAdapter);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    //只有在登录的情况下 才请求记录
    private void requestFeedbackList() {
        if (LocalUser.getUser().isLogin()) {
            Client.getFeedback(mPage, mPageSize)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<List<Feedback>>, List<Feedback>>() {
                        @Override
                        protected void onRespSuccessData(List<Feedback> data) {
                            updateFeedbackList(data);
                        }

                        @Override
                        public void onFailure(VolleyError volleyError) {
                            super.onFailure(volleyError);
                            stopRefreshAnimation();
                        }
                    })
                    .fire();
        }
    }

    private void updateFeedbackList(List<Feedback> data) {
        if (data == null) {
            stopRefreshAnimation();
            return;
        }

        if (data.size() < mPageSize) {
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mPage++;
            mFeedbackAdapter.addFeedbackList(data);
        }
        mFeedbackAdapter.addFeedbackList(data);

    }

    @Override
    public void onRefresh() {
        requestFeedbackList();
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
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    @OnClick({R.id.send, R.id.addPic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                String content = mCommentContent.getText().toString().trim();
                requestSendFeedback(content, CONTENT_TYPE_TEXT);
                break;
            case R.id.addPic:
                sendPicToCustomer();
                break;
        }
    }

    private void requestSendFeedback(String content, int contentType) {
        Client.sendFeedback(content, contentType)
                .setTag(TAG)
                .setIndeterminate(FeedbackActivity.this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        // TODO: 2017/5/10 刷新界面
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }
                })
                .fireSync();
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

    private void requestSendFeedbackImage(String path) {
        Glide.with(this).load("file://" + path).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                String content = ImageUtils.bitmapToBase64(resource);
                int contentType = CONTENT_TYPE_PICTURE;
                requestSendFeedback(content, contentType);
            }
        });
    }


    private static class FeedbackAdapter extends BaseAdapter {

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
            for (int i = length - 1; i > 0; i--) {
                mFeedbackList.add(0, list.get(i));
            }
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserViewHolder userViewHolder = null;
            CustomerViewHolder customerViewHolder = null;
            switch (getItemViewType(position)) {
                case TYPE_USER:
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.row_feedback_user, null, false);
                        userViewHolder = new UserViewHolder(convertView);
                        convertView.setTag(userViewHolder);
                    } else {
                        userViewHolder = (UserViewHolder) convertView.getTag();
                    }
                    userViewHolder.bindingData(position, mFeedbackList);
                    break;
                case TYPE_CUSTOMER:
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.row_feedback_user, null, false);
                        customerViewHolder = new CustomerViewHolder(convertView);
                        convertView.setTag(customerViewHolder);
                    } else {
                        customerViewHolder = (CustomerViewHolder) convertView.getTag();
                    }
                    customerViewHolder.bindingData(position, mFeedbackList);
                    break;
            }
            return convertView;
        }

        static class UserViewHolder {
            public UserViewHolder(View view) {

            }

            private void bindingData(int position, List<Feedback> list) {

            }
        }

        static class CustomerViewHolder {
            public CustomerViewHolder(View view) {

            }

            private void bindingData(int position, List<Feedback> list) {

            }
        }
    }

}
