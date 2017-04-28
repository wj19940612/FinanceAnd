package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.BorrowHelper;
import com.sbai.finance.model.BorrowIn;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowInActivity extends BaseActivity {
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBorrowInData();
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
    private void requestHelper(Integer id, final int position){
        Client.getHelper(id).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowHelper>>,List<BorrowHelper>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowHelper> data) {
                        updateHelperData(data,position);
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
    }
    private void updateHelperData(List<BorrowHelper> data,int position){

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
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position),position,getContext());
            TextView cancelBorrow = (TextView) convertView.findViewById(R.id.cancelBorrowIn);
            cancelBorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.OnItemCancelBorrowClick(getItem(position).getId());
                }
            });
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
                mPublishTime.setText(DateUtil.formatSlash(item.getCreateTime()));
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                mOption.setText(item.getContent());

                SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(context.getString(R.string.call_helper),
                        "\n" +context.getString(R.string.end_line)," "+item.getEndlineTime(), 1.455f,1.455f,
                        ContextCompat.getColor(context,R.color.opinionText), ContextCompat.getColor(context,R.color.redPrimary));
                mEndLineTime.setText(attentionSpannableString);

                String[] images = item.getContentImg().split(",");
                switch (images.length){
                    case 0:
                        mImage1.setVisibility(View.INVISIBLE);
                        mImage2.setVisibility(View.INVISIBLE);
                        mImage3.setVisibility(View.INVISIBLE);
                        mImage4.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        mImage1.setVisibility(View.VISIBLE);
                        loadImage(context,images[0],mImage1);
                        mImage2.setVisibility(View.INVISIBLE);
                        mImage3.setVisibility(View.INVISIBLE);
                        mImage4.setVisibility(View.INVISIBLE);
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
                Glide.with(context).load(src).placeholder(R.drawable.help).into(image);
            }
        }
    }
}
