package com.sbai.finance.activity.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.training.TrainingDetailActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.MyTrainingRecord;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AllTrainingListActivity extends BaseActivity {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TrainAdapter mTrainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_training_list);
        ButterKnife.bind(this);

        mTrainAdapter = new TrainAdapter(this);
        mList.setEmptyView(mEmpty);
        mList.setAdapter(mTrainAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAllTrainingList();
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTrainingRecord myTrainingRecord = (MyTrainingRecord) parent.getAdapter().getItem(position);
                if(myTrainingRecord!=null){
                    Launcher.with(getActivity(), TrainingDetailActivity.class)
                            .putExtra(ExtraKeys.TRAINING, myTrainingRecord.getTrain())
                            .execute();
                    finish();
                }
            }
        });
        requestAllTrainingList();
    }

    private void requestAllTrainingList() {
        Client.getTrainingList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MyTrainingRecord>>, List<MyTrainingRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<MyTrainingRecord> data) {
                        updateTrainData(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }).fireFree();
    }

    private void updateTrainData(List<MyTrainingRecord> data) {
        mTrainAdapter.clear();
        for (MyTrainingRecord trainingRecord : data) {
            if (trainingRecord.getTrain() != null) {
                mTrainAdapter.add(trainingRecord);
            }
        }
    }

    public static class TrainAdapter extends ArrayAdapter<MyTrainingRecord> {

        public TrainAdapter(@NonNull Context context) {
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

            private void bindDataWithView(MyTrainingRecord item, Context context) {
                Glide.with(context)
                        .load(item.getTrain().getImage2Url())
                        .into(mTrainImg);
                mTrainType.setText(item.getTrain().getTitle());
                if (!LocalUser.getUser().isLogin()) {
                    mTrainCount.setText(context.getString(R.string.train_count, 0));
                } else {
                    if (item.getRecord() == null || item.getRecord().getFinish() == 0) {
                        mTrainCount.setText(context.getString(R.string.have_no_train));
                    } else {
                        mTrainCount.setText(context.getString(R.string.train_count, item.getRecord().getFinish()));
                    }
                }
                mGrade.setText(context.getString(R.string.level, item.getTrain().getLevel()));
                switch (item.getTrain().getType()) {
                    case Training.TYPE_THEORY:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#FFB269"), Color.parseColor("#FB857A")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#FFB269"), Color.parseColor("#FB857A")}, context));
                        }
                        break;
                    case Training.TYPE_TECHNOLOGY:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#A485FF"), Color.parseColor("#C05DD8")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#A485FF"), Color.parseColor("#C05DD8")}, context));
                        }
                        break;
                    case Training.TYPE_FUNDAMENTAL:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#EEA259"), Color.parseColor("#FDD35E")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#EEA259"), Color.parseColor("#FDD35E")}, context));
                        }
                        break;
                    case Training.TYPE_COMPREHENSIVE:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#64A0FE"), Color.parseColor("#995BF4")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#64A0FE"), Color.parseColor("#995BF4")}, context));
                        }
                        break;
                }
            }

            private Drawable createDrawable(int[] colors, Context context) {
                GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
                gradient.setCornerRadius(Display.dp2Px(8, context.getResources()));
                return gradient;
            }
        }
    }
}
