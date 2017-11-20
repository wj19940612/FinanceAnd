package com.sbai.finance.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.home.AllTrainingListActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.SubscribeModel;
import com.sbai.finance.model.training.MyTrainingRecord;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.utils.Display;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\11\20 0020.
 */

public class MySubscribeActivity extends Activity {
    @BindView(android.R.id.list)
    ListView mList;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_subscribe);
        ButterKnife.bind(this);
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_discover_train, parent, false);
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
            CardView mContent;
            @BindView(R.id.trainImg)
            ImageView mTrainImg;
            @BindView(R.id.grade)
            TextView mGrade;
            @BindView(R.id.trainType)
            TextView mTrainType;
            @BindView(R.id.trainCount)
            TextView mTrainCount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(SubscribeModel item, Context context) {

                
            }
        }
    }
}
