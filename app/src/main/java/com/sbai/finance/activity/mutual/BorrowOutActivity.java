package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.BorrowOut;
import com.sbai.finance.model.LocalUser;
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

import static android.R.attr.data;

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
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mBorrowOutAdapter);
        mListView.setOnScrollListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBorrowOutData();
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
        if (data.isEmpty()){
            stopRefreshAnimation();
        }
        mBorrowOutAdapter.clear();
        mBorrowOutAdapter.addAll( data);
        mBorrowOutAdapter.notifyDataSetChanged();
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
        public BorrowOutAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_borrow_out_mine, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position),getContext());
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
            private void bindDataWithView(BorrowOut item, Context context){
                Glide.with(context).load(item.getContentImg())
                        .placeholder(R.drawable.ic_default_avatar)
                        .bitmapTransform(new GlideCircleTransform(context))
                        .into(mUserPortrait);
                mPublishTime.setText(context.getString(R.string.borrow_out_time,DateUtil.formatSlash(item.getConfirmTime())));
                mNeedAmount.setText(context.getString(R.string.RMB,String.valueOf(item.getMoney())));
                mBorrowTime.setText(context.getString(R.string.day,String.valueOf(item.getDays())));
                mBorrowInterest.setText(context.getString(R.string.RMB,String.valueOf(item.getInterest())));
                mOption.setText(item.getContent());

                SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(item.getUserName(),
                        "\n" +"吴彦祖", 0.733f, ContextCompat.getColor(context,R.color.assistText));
                mUserNameLand.setText(attentionSpannableString);
                mEndLineTime.setText("11:44");

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
                Glide.with(context).load(src)
                        .placeholder(R.drawable.help)
                        .into(image);
            }

        }
    }

}
