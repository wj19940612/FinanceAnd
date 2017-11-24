package com.sbai.finance.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.training.TrainingDetailActivity;
import com.sbai.finance.model.SubscribeModel;
import com.sbai.finance.model.training.MyTrainingRecord;
import com.sbai.finance.utils.Launcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\11\20 0020.
 */

public class MySubscribeActivity extends BaseActivity {
    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private SubscribeAdapter mSubscribeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_subscribe);
        ButterKnife.bind(this);

        mSubscribeAdapter = new SubscribeAdapter(this);
        mList.setEmptyView(mEmpty);
        mList.setAdapter(mSubscribeAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestSubscribeList();
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubscribeModel subscribeModel = (SubscribeModel) parent.getAdapter().getItem(position);

            }
        });
        requestSubscribeList();
    }

    private void requestSubscribeList(){
        //TODO 获取数据
        List<SubscribeModel> subscribeModels;
    }

    private void setSpanIconText(TextView textView , String str){
        SpannableString ss = new SpannableString(str);

        Drawable drawable = getResources().getDrawable(R.drawable.battle_banner);
        drawable.setBounds(0,0,30,30);
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, 0,1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
    }

    public static class SubscribeAdapter extends ArrayAdapter<SubscribeModel> {
        public SubscribeAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_mine_subscribe, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.cover)
            ImageView mCover;
            @BindView(R.id.subtitle)
            TextView mSubtitle;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.number)
            TextView mNumber;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.iconView)
            View mIconView;
            @BindView(R.id.content)
            RelativeLayout mContent;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(SubscribeModel item, Context context) {


            }
        }
    }
}
