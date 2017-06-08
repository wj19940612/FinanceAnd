package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EconomicCircleNewsActivity extends BaseActivity {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private EconomicCircleNewsAdapter mEconomicCircleNewsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_economic_circle_news);
        ButterKnife.bind(this);
        mListView.setEmptyView(mEmpty);
        mEconomicCircleNewsAdapter = new EconomicCircleNewsAdapter(this);
        mListView.setAdapter(mEconomicCircleNewsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        requestEconomicCircleNewsList();
    }

    private void requestEconomicCircleNewsList() {



    }

    static class EconomicCircleNewsAdapter extends ArrayAdapter<HistoryNewsModel> {

        public EconomicCircleNewsAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_economic_circle_news, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), parent.getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.userHeadImage)
            AppCompatImageView mUserHeadImage;
            @BindView(R.id.userAction)
            AppCompatTextView mUserAction;
            @BindView(R.id.content)
            AppCompatTextView mContent;
            @BindView(R.id.time)
            AppCompatTextView mTime;
            @BindView(R.id.presentation)
            AppCompatTextView mPresentation;
            @BindView(R.id.rightPicture)
            AppCompatImageView mRightPicture;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(HistoryNewsModel item, Context context) {
                if (item == null) return;
                UserInfo userInfo = item.getUserInfo();
                if (userInfo != null) {
                    Glide.with(context).load(userInfo.getUserPortrait())
                            .bitmapTransform(new GlideCircleTransform(context))
                            .placeholder(R.drawable.ic_default_avatar)
                            .into(mUserHeadImage);
                    mUserAction.setText(userInfo.getUserName());
                }
                mContent.setText(item.getMsg());
                mTime.setText(DateUtil.getFormatTime(item.getCreateDate()));

                // TODO: 2017/6/8 借款需要分开
            }
        }
    }
}
