package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mutual.BorrowHelper;
import com.sbai.finance.model.mutual.BorrowIn;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.MyGridView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BorrowInActivity extends BaseActivity implements AbsListView.OnScrollListener {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    private BorrowInAdapter mBorrowInAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine);
        ButterKnife.bind(this);
        initView();
        requestBorrowInData();
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBorrowInData();
            }
        });
        mBorrowInAdapter = new BorrowInAdapter(this);
        mBorrowInAdapter.setCallback(new BorrowInAdapter.Callback() {
            @Override
            public void OnItemCancelBorrowClick(Integer id) {
                requestCancelBorrow(id);
            }

            @Override
            public void OnItemGetHelper(Integer id, int position) {
                requestHelper(id,position);
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowInAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Launcher.with(getActivity(),BorrowInDetailsActivity.class)
                        .putExtra(BorrowInDetailsActivity.BORROW_IN,mBorrowInAdapter.getItem(position))
                        .execute();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestBorrowInData(){
        Client.getBorrowInList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowIn>>,List<BorrowIn>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowIn> data) {
                        updateBorrowInData(data);
                    }
                })
                .fire();
    }
    private void requestHelper(final int id, final int position){
        Client.getHelper(id).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowHelper>>,List<BorrowHelper>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowHelper> data) {
                        updateHelperData(id,data,position);
                    }
                }).fire();
    }
    private void requestCancelBorrow(Integer id){
        Client.cancelBorrowIn(id).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()){
                            requestBorrowInData();
                        }else{
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fire();
    }

    private void updateBorrowInData(List<BorrowIn> data) {
        stopRefreshAnimation();
        mBorrowInAdapter.clear();
        mBorrowInAdapter.addAll(data);
        mBorrowInAdapter.notifyDataSetChanged();
        startScheduleJob(1000*60);
    }
    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (mListView!=null&&mBorrowInAdapter!=null&&mBorrowInAdapter.getCount()==0){
            stopScheduleJob();
            return;
        }
        updateEndLineData();
    }
    private void updateEndLineData(){
        if(mListView!=null&&mBorrowInAdapter!=null){
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i <= last; i++) {
                BorrowIn borrowIn = mBorrowInAdapter.getItem(i);
                View childView = mListView.getChildAt(i);
                if (borrowIn!=null&&borrowIn.getStatus()==BorrowIn.STATUS_CHECKED&&childView!=null){
                    TextView mEndLineTime = (TextView) childView.findViewById(R.id.endLineTime);
                    SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(getActivity().getString(R.string.call_helper),
                            "\n" +getActivity().getString(R.string.end_line),"  "+DateUtil.compareTime(borrowIn.getEndlineTime()), 1.455f,1.455f,
                            ContextCompat.getColor(getActivity(),R.color.opinionText), ContextCompat.getColor(getActivity(),R.color.redPrimary));
                    mEndLineTime.setText(attentionSpannableString);
                }
            }
        }
    }
    private void updateHelperData(Integer id, List<BorrowHelper> data, int position){
         if(mListView!=null&&mBorrowInAdapter!=null){
             BorrowIn borrowIn = mBorrowInAdapter.getItem(position);
             View childView = mListView.getChildAt(position);
             if(borrowIn!=null&&borrowIn.getId()==id&&childView!=null){
                 TextView helperAmount = (TextView) childView.findViewById(R.id.helperAmount);
                 MyGridView mGridView= (MyGridView) childView.findViewById(R.id.gridView);
                 helperAmount.setText(getActivity().getString(R.string.helper,data.size()));

                 ImageGridAdapter imageGridAdapter= (ImageGridAdapter) mGridView.getAdapter();
                 if (imageGridAdapter==null){
                     imageGridAdapter = new ImageGridAdapter(this);
                     imageGridAdapter.setOnItemClickListener(new ImageGridAdapter.OnItemClickListener() {
                         @Override
                         public void onClick(BorrowHelper item) {
                             ToastUtil.show(item.getUserName());
                         }
                     });
                     mGridView.setAdapter(imageGridAdapter);
                 }
                 imageGridAdapter.clear();
                 imageGridAdapter.addAll(data);
                 imageGridAdapter.notifyDataSetChanged();

             }
         }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    @OnClick(R.id.borrowInHis)
    public void onClick(View view){
        Launcher.with(getActivity(),BorrowInHisActivity.class).execute();
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
    static class ImageGridAdapter extends ArrayAdapter<BorrowHelper> {

        public ImageGridAdapter(@NonNull Context context) {
            super(context, 0);
        }
        interface OnItemClickListener{
            void onClick(BorrowHelper item);
        }
        private OnItemClickListener mOnItemClickListener;
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            mOnItemClickListener = onItemClickListener;
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_helper_image, null);
                ImageView userImage = (ImageView) convertView.findViewById(R.id.userImg);
                userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onClick(getItem(position));
                    }
                });
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position),getContext());
            return convertView;
        }
        static class ViewHolder{
            @BindView(R.id.userImg)
            ImageView mUserImg;
            ViewHolder(View view){
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(BorrowHelper item, Context context){
                Glide.with(context).load(item.getPortrait())
                        .bitmapTransform(new GlideCircleTransform(context))
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(mUserImg);
            }
        }
    }

    static class BorrowInAdapter extends ArrayAdapter<BorrowIn>{
        Context mContext;
        private Callback mCallback;
        interface Callback{
            void OnItemCancelBorrowClick(Integer id);
            void OnItemGetHelper(Integer id,int position );
        }
        public void setCallback(Callback callback){
            mCallback = callback;
        }
        public BorrowInAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_in_mine, parent, false);
                TextView cancelBorrow = (TextView) convertView.findViewById(R.id.cancelBorrowIn);
                cancelBorrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.OnItemCancelBorrowClick(getItem(position).getId());
                    }
                });

                MyGridView mGridView= (MyGridView) convertView.findViewById(R.id.gridView);
                ImageGridAdapter imageGridAdapter = new ImageGridAdapter(getContext());
                imageGridAdapter.setOnItemClickListener(new ImageGridAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(BorrowHelper item) {
                        ToastUtil.show(item.getUserName());
                    }
                });
                mGridView.setAdapter(imageGridAdapter);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position),position,getContext());
            mCallback.OnItemGetHelper(getItem(position).getId(),position);
            return convertView;
        }

        static class ViewHolder{
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
            @BindView(R.id.cancelBorrowIn)
            TextView mCancelBorrowIn;
            @BindView(R.id.helperAmount)
            TextView mHelperAmount;
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
            private void bindDataWithView(BorrowIn item, int position, Context context){

                mPublishTime.setText(context.getString(R.string.publish_time,DateUtil.formatSlash(item.getCreateDate())));
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                mHelperAmount.setText(context.getString(R.string.helper,item.getIntentionCount()));
                mOption.setText(item.getContent());
                SpannableString attentionSpannableString;
                switch (item.getStatus()){
                    case BorrowIn.STATUS_CHECKED:
                        attentionSpannableString = StrUtil.mergeTextWithRatioColor(context.getString(R.string.call_helper),
                                "\n" +context.getString(R.string.end_line),"  "+DateUtil.compareTime(item.getEndlineTime()), 1.455f,1.455f,
                                ContextCompat.getColor(context,R.color.opinionText), ContextCompat.getColor(context,R.color.redPrimary));
                        mEndLineTime.setText(attentionSpannableString);
                        mCancelBorrowIn.setVisibility(View.VISIBLE);
                        break;
                    case BorrowIn.STATUS_NO_CHECK:
                        attentionSpannableString = StrUtil.mergeTextWithColor(context.getString(R.string.on_checking),1.455f,
                                ContextCompat.getColor(context,R.color.opinionText));
                        mEndLineTime.setText(attentionSpannableString);
                        mCancelBorrowIn.setVisibility(View.GONE);
                        break;
                    default:
                        break;
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
            }
            private void loadImage(Context context,String src,ImageView image){
                Glide.with(context).load(src).placeholder(R.drawable.ic_default_avatar).into(image);
            }
        }
    }
}
