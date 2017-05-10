package com.sbai.finance.activity.opinion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.ViewPointMater;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class OpinionActivity extends BaseActivity implements AbsListView.OnScrollListener  {
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty)
    TextView mEmpty;
    private  OpinionListAdapter mOpinionListAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOpinionMasterData();
            }
        });
        mOpinionListAdapter = new OpinionListAdapter(this);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mOpinionListAdapter);
        mListView.setOnScrollListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestOpinionMasterData();
    }

    private void requestOpinionMasterData() {
        Client.getViewPointMaster().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<ViewPointMater>>,List<ViewPointMater>>() {
                    @Override
                    protected void onRespSuccessData(List<ViewPointMater> data) {
                        updateOpinionInfo(data);
                    }
                }).fireSync();
    }

    private void updateOpinionInfo(List<ViewPointMater> data) {
        stopRefreshAnimation();
        mOpinionListAdapter.clear();
        mOpinionListAdapter.addAll(data);
        mOpinionListAdapter.notifyDataSetChanged();
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
    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    public static class OpinionListAdapter extends ArrayAdapter<ViewPointMater> {
        Context mContext;
        public OpinionListAdapter(@NonNull Context context){
            super(context,0);
            mContext = context;
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_opinion_master, parent, false);
                viewHolder = new  ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder =(ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }


        static class ViewHolder{
            @BindView(R.id.userImg)
            ImageView mUserImg;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.skilledType)
            TextView mSkilledType;
            @BindView(R.id.accuracyRate)
            TextView mAccuracyRate;
            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(ViewPointMater item, Context context) {
                Glide.with(context).load(item.getUserPortrait())
                        .bitmapTransform(new GlideCircleTransform(context))
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(mUserImg);
                mUserName.setText(item.getUserName());
                if(item.getAdeptType()!=null){
                    switch (item.getAdeptType()){
                        case ViewPointMater.TYPE_FUTURE:
                            mSkilledType.setText(context.getString(R.string.future));
                            break;
                        case ViewPointMater.TYPE_STOCK:
                            mSkilledType.setText(context.getString(R.string.stock));
                            break;
                        case ViewPointMater.TYPE_FOREX:
                            mSkilledType.setText(context.getString(R.string.forex));
                            break;
                        default:
                            break;
                     }
                }else{
                    mSkilledType.setText(context.getString(R.string.future));
                }
                mAccuracyRate.setText("+"+String.valueOf(item.getPassRat()*100)+"%");

            }
        }
    }

}
