package com.sbai.finance.fragment.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.StockViewPointModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/5/10.
 */

public class ViewPointFragment extends BaseFragment {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_refresh_listview, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setEmptyView(mEmpty);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private static class ViewPointAdapter extends ArrayAdapter<StockViewPointModel> {

        private Context mContext;

        public ViewPointAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_viewpoint_list, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.isAttention)
            TextView mIsAttention;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.opinionContent)
            TextView mOpinionContent;
            @BindView(R.id.replyCount)
            AppCompatTextView mReplyCount;
            @BindView(R.id.praiseCount)
            AppCompatTextView mPraiseCount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(StockViewPointModel item) {

            }
        }
    }
}
