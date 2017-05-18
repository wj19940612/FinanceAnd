package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.mutual.BorrowOut;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BorrowOutActivity extends BaseActivity  implements AbsListView.OnScrollListener{
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    private BorrowOutAdapter mBorrowOutAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_out_mine);
        ButterKnife.bind(this);
        initView();
        requestBorrowOutData();
    }
    @OnClick(R.id.borrowOutHis)
    public void onClick(View view){
        Launcher.with(this,BorrowOutHisActivity.class).execute();
    }
    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBorrowOutData();
            }
        });
        mBorrowOutAdapter = new BorrowOutAdapter(this);
        mBorrowOutAdapter.setCallback(new BorrowOutAdapter.Callback() {
            @Override
            public void OnItemUserClick(int userId) {
                Launcher.with(getActivity(), UserDataActivity.class).putExtra(Launcher.USER_ID, userId).execute();
            }

            @Override
            public void OnItemImageClick(int index, String  headImage) {
                Launcher.with(getActivity(), ContentImgActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD,headImage.split(","))
                        .putExtra(Launcher.EX_PAYLOAD_1,index)
                        .execute();
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowOutAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Launcher.with(getActivity(),BorrowOutDetailsActivity.class)
                        .putExtra(BorrowOutDetailsActivity.ID,mBorrowOutAdapter.getItem(position).getId())
                        .putExtra(BorrowOutDetailsActivity.INTENTION_TIME,mBorrowOutAdapter.getItem(position).getIntentionTime())
                        .execute();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestBorrowOutData() {
        Client.getBorrowOutList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowOut>>,List<BorrowOut>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowOut> data) {
                        updateBorrowOut(data);
                    }
                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateBorrowOut(List<BorrowOut> data) {
        stopRefreshAnimation();
        mBorrowOutAdapter.clear();
        mBorrowOutAdapter.addAll( data);
        mBorrowOutAdapter.notifyDataSetChanged();
        startScheduleJob(1000*60);
    }
    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (mListView!=null&&mBorrowOutAdapter!=null&&mBorrowOutAdapter.getCount()==0){
            stopScheduleJob();
            return;
        }
        updateEndLineData();
    }
    private void updateEndLineData(){
        if(mListView!=null&&mBorrowOutAdapter!=null){
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i <= last; i++) {
                BorrowOut borrowOut = mBorrowOutAdapter.getItem(i);
                View childView = mListView.getChildAt(i);
                if (borrowOut!=null&&childView!=null){
                    TextView mEndLineTime = (TextView) childView.findViewById(R.id.endLineTime);
                    mEndLineTime.setText(DateUtil.compareTime(borrowOut.getEndlineTime()));
                }
            }
        }
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

    static class BorrowOutAdapter extends ArrayAdapter<BorrowOut>{

        Context mContext;
        private Callback mCallback;
        interface Callback{
            void OnItemUserClick(int userId);
            void OnItemImageClick(int index ,String headImage);
        }
        public void setCallback(Callback callback){
            mCallback = callback;
        }
        public BorrowOutAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_out_mine, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position),getContext(),mCallback);
            return convertView;
        }

        static class ViewHolder{
            @BindView(R.id.userPortrait)
            ImageView mUserPortrait;
            @BindView(R.id.userNameLand)
            TextView mUserNameLand;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.needAmount)
            TextView mNeedAmount;
            @BindView(R.id.borrowTime)
            TextView mBorrowTime;
            @BindView(R.id.borrowInterest)
            TextView mBorrowInterest;
            @BindView(R.id.endLineTime)
            TextView mEndLineTime;
            @BindView(R.id.opinion)
            TextView mOption;
            @BindView(R.id.image1)
            ImageView mImage1;
            @BindView(R.id.image2)
            ImageView mImage2;
            @BindView(R.id.image3)
            ImageView mImage3;
            @BindView(R.id.image4)
            ImageView mImage4;
            ViewHolder(View view){
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(final BorrowOut item, Context context, final Callback callback){
                Glide.with(context).load(item.getPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mUserPortrait);
                mPublishTime.setText(context.getString(R.string.borrow_out_time,DateUtil.getFormatTime(item.getIntentionTime())));
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                mOption.setText(item.getContent());
                String location = item.getLocation();
                if(TextUtils.isEmpty(location)){
                    location = context.getString(R.string.no_location);
                }
                SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(item.getUserName(),
                        "\n" +location, 0.733f, ContextCompat.getColor(context,R.color.assistText));
                mUserNameLand.setText(attentionSpannableString);
                mEndLineTime.setText(DateUtil.compareTime(item.getEndlineTime()));
                if (item.getContentImg()==null){
                    item.setContentImg("");
                }
                String[] images = item.getContentImg().split(",");
                switch (images.length){
                    case 1:
                        if (TextUtils.isEmpty(images[0])){
                            mImage1.setVisibility(View.GONE);
                            mImage2.setVisibility(View.GONE);
                            mImage3.setVisibility(View.GONE);
                            mImage4.setVisibility(View.GONE);
                        }else{
                            mImage1.setVisibility(View.VISIBLE);
                            loadImage(context,images[0],mImage1);
                            mImage2.setVisibility(View.INVISIBLE);
                            mImage3.setVisibility(View.INVISIBLE);
                            mImage4.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 2:
                        mImage1.setVisibility(View.VISIBLE);
                        loadImage(context,images[0],mImage1);
                        mImage2.setVisibility(View.VISIBLE);
                        loadImage(context,images[1],mImage2);
                        mImage3.setVisibility(View.INVISIBLE);
                        mImage4.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        mImage1.setVisibility(View.VISIBLE);
                        loadImage(context,images[0],mImage1);
                        mImage2.setVisibility(View.VISIBLE);
                        loadImage(context,images[1],mImage2);
                        mImage3.setVisibility(View.VISIBLE);
                        loadImage(context,images[2],mImage3);
                        mImage4.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        mImage1.setVisibility(View.VISIBLE);
                        loadImage(context,images[0],mImage1);
                        mImage2.setVisibility(View.VISIBLE);
                        loadImage(context,images[1],mImage2);
                        mImage3.setVisibility(View.VISIBLE);
                        loadImage(context,images[2],mImage3);
                        mImage4.setVisibility(View.VISIBLE);
                        loadImage(context,images[3],mImage4);
                        break;
                    default:
                        break;

                }
                mImage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(0,item.getContentImg());
                    }
                });
                mImage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(1,item.getContentImg());
                    }
                });
                mImage3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(2,item.getContentImg());
                    }
                });
                mImage4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemImageClick(3,item.getContentImg());
                    }
                });
                mUserPortrait.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.OnItemUserClick(item.getUserId());
                    }
                });
            }
            private void loadImage(Context context,String src,ImageView image){
                Glide.with(context).load(src)
                        .placeholder(R.drawable.help)
                        .into(image);
            }

        }
    }

}
