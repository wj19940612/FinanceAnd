package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.Comment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpinionDetailsActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.followed)
    TextView mFollowed;
    @BindView(R.id.publishTime)
    TextView mPublishTime;
    @BindView(R.id.opinion)
    TextView mOpinion;
    @BindView(R.id.product)
    TextView mProduct;
    @BindView(R.id.productName)
    TextView mProductName;
    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.upDownPrice)
    TextView mUpDownPrice;
    @BindView(R.id.upDownPercent)
    TextView mUpDownPercent;
    @BindView(R.id.like)
    TextView mLike;
    @BindView(R.id.commentNum)
    TextView mCommentNum;
    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.comment)
    EditText mComment;
    @BindView(R.id.reply)
    TextView mReply;

    private List<Comment> mCommentList;
    private CommentAdapter mCommentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_details);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCommentList = new ArrayList<>();
        initData();
        mCommentAdapter = new CommentAdapter(this, mCommentList);
        //mList.setEmptyView(mEmpty);
        mList.setAdapter(mCommentAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mList.setNestedScrollingEnabled(true);
        }

    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mCommentList.add(new Comment("哈哈"));
        }
    }

    private void initView() {
        mUserName.setText("刘亦菲");
        mFollowed.setText("已关注");
        mPublishTime.setText("战国");
        mOpinion.setText("黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨黄金涨涨涨");
        mProduct.setText("股票");
        mProductName.setText("松柏信息");
        mLastPrice.setText("88.88");
        mUpDownPrice.setText("+8.8");
        mUpDownPercent.setText("+10%");
        mLike.setText("8888");
        mCommentNum.setText(getString(R.string.comment_number, "88"));

    }

    static class CommentAdapter extends BaseAdapter {
        private Context mContext;
        private List<Comment> mCommentList;

        private CommentAdapter(Context context, List<Comment> commentList) {
            this.mContext = context;
            this.mCommentList = commentList;
        }

        @Override
        public int getCount() {
            return mCommentList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCommentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_opinion_details, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(mContext, (Comment) getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.followed)
            TextView mFollowed;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.opinion)
            TextView mOpinion;
            @BindView(R.id.like)
            TextView mLike;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, Comment item) {
                mUserName.setText("刘亦菲");
                mFollowed.setText("已关注");
                mPublishTime.setText("战国");
                mOpinion.setText("话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。");
                mLike.setText("8888");
                mLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLike.isSelected()) {
                            mLike.setSelected(false);
                        } else {
                            mLike.setSelected(true);
                        }
                    }
                });
            }
        }
    }

    @OnClick({R.id.like, R.id.reply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.like:
                if (mLike.isSelected()) {
                    mLike.setSelected(false);
                } else {
                    mLike.setSelected(true);
                }
                break;
            case R.id.reply:
                break;
        }
    }
}
