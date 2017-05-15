package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.mutual.BorrowDetails;
import com.sbai.finance.model.mutual.BorrowHelper;
import com.sbai.finance.model.mutual.BorrowIn;
import com.sbai.finance.model.mutual.BorrowInHis;
import com.sbai.finance.model.mutual.CallPhone;
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

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowInHisDetailActivity extends BaseActivity {
    public static final String BORROW_IN_HIS="borrowInHis";
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
    @BindView(R.id.opinion)
    TextView mOption;
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
    @BindView(R.id.gridView)
    MyGridView mGridView;
    private ImageGridAdapter mImageGridAdapter;
    private int mSelectedId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine_his_details);
        ButterKnife.bind(this);
        initView();
        int id= getIntent().getIntExtra(BORROW_IN_HIS,-1);
        if (id!=-1){
            requestBorrowDetail(id);
            requestHelper(id);
        }
    }
    private void initView() {
        mImageGridAdapter = new ImageGridAdapter(this);
        mGridView.setAdapter(mImageGridAdapter);
        mGridView.setFocusable(false);
    }
    private void requestBorrowDetail(int id){
       Client.getBorrowDetails(id).setTag(TAG)
               .setCallback(new Callback2D<Resp<BorrowDetails>,BorrowDetails>() {
                   @Override
                   protected void onRespSuccessData(BorrowDetails data) {
                        updateBorrowDetailData(data);
                   }
               }).fireSync();

    }
    private void requestHelper( int id){
        Client.getHelper(id).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BorrowHelper>>,List<BorrowHelper>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowHelper> data) {
                        updateHelperData(data);
                    }
                }).fire();
    }
    private void updateBorrowDetailData(BorrowDetails data) {
        mSelectedId = data.getSelectedUserId();
        Glide.with(this).load(data.getSelectedPortrait())
                .bitmapTransform(new GlideCircleTransform(this))
                .placeholder(R.drawable.ic_default_avatar).into(mUserPortrait);

        String location =data.getSelectedLocation();
        if (location==null){
            location = this.getString(R.string.no_location);
        }
        SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(data.getSelectedUserName(), "\n"+ location,0.73f,
                ContextCompat.getColor(this,R.color.redPrimary),ContextCompat.getColor(this,R.color.assistText));
        mUserNameLand.setText(attentionSpannableString);
        mPublishTime.setText(this.getString(R.string.borrow_in_time,
                this.getString(R.string.borrow_in_time_success), DateUtil.formatSlash(data.getAuditTime())));
        mNeedAmount.setText(getActivity().getString(R.string.RMB,String.valueOf(data.getMoney())));
        mBorrowTime.setText(getActivity().getString(R.string.day,String.valueOf(data.getDays())));
        mBorrowInterest.setText(getActivity().getString(R.string.RMB,String.valueOf(data.getInterest())));
        mHelperAmount.setText(getActivity().getString(R.string.helper,data.getIntentionCount()));
        mOption.setText(data.getContent());
        String[] images = data.getContentImg().split(",");
        switch (images.length){
            case 1:
                if (TextUtils.isEmpty(images[0])){
                    mImage1.setVisibility(View.GONE);
                    mImage2.setVisibility(View.GONE);
                    mImage3.setVisibility(View.GONE);
                    mImage4.setVisibility(View.GONE);
                }else{
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage(images[0],mImage1);
                    mImage2.setVisibility(View.INVISIBLE);
                    mImage3.setVisibility(View.INVISIBLE);
                    mImage4.setVisibility(View.INVISIBLE);
                }
                break;
            case 2:
                mImage1.setVisibility(View.VISIBLE);
                loadImage(images[0],mImage1);
                mImage2.setVisibility(View.VISIBLE);
                loadImage(images[1],mImage2);
                mImage3.setVisibility(View.INVISIBLE);
                mImage4.setVisibility(View.INVISIBLE);
                break;
            case 3:
                mImage1.setVisibility(View.VISIBLE);
                loadImage(images[0],mImage1);
                mImage2.setVisibility(View.VISIBLE);
                loadImage(images[1],mImage2);
                mImage3.setVisibility(View.VISIBLE);
                loadImage(images[2],mImage3);
                mImage4.setVisibility(View.INVISIBLE);
                break;
            case 4:
                mImage1.setVisibility(View.VISIBLE);
                loadImage(images[0],mImage1);
                mImage2.setVisibility(View.VISIBLE);
                loadImage(images[1],mImage2);
                mImage3.setVisibility(View.VISIBLE);
                loadImage(images[2],mImage3);
                mImage4.setVisibility(View.VISIBLE);
                loadImage(images[3],mImage4);
                break;
            default:
                break;

        }

    }
    private void loadImage(String src,ImageView image){
        Glide.with(this).load(src).placeholder(R.drawable.help).into(image);
    }

    private void updateHelperData(List<BorrowHelper> data) {
        mHelperAmount.setText(getActivity().getString(R.string.helper,data.size()));
        mImageGridAdapter.clear();
        mImageGridAdapter.addAll(data);
        mImageGridAdapter.notifyDataSetChanged();
    }
    @OnClick(R.id.userPortrait)
    public void onClick(View view){
        Launcher.with(getActivity(),UserDataActivity.class).putExtra(Launcher.USER_ID,mSelectedId).execute();
    }
    static class ImageGridAdapter extends ArrayAdapter<BorrowHelper> {

        public ImageGridAdapter(@NonNull Context context) {
            super(context, 0);
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_helper_image, null);
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
}
