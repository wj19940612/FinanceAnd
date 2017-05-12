package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.mutual.BorrowHelper;
import com.sbai.finance.model.mutual.BorrowOut;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.MyGridView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BorrowOutDetailsActivity extends BaseActivity {
    public static final String BORROW_OUT="borrowOut";
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
    private BorrowOut mBorrowOut;
    private BorrowInDetailsActivity.ImageGridAdapter mImageGridAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_out_mine_details);
        ButterKnife.bind(this);
        initView();
        mBorrowOut = getIntent().getParcelableExtra(BORROW_OUT);
        if (mBorrowOut!=null){
            updateDataWithView(mBorrowOut);
            requestHelper(mBorrowOut.getId());
        }
    }

    private void initView() {
        mImageGridAdapter = new BorrowInDetailsActivity.ImageGridAdapter(this);
        mGridView.setAdapter(mImageGridAdapter);
        mGridView.setFocusable(false);
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
    private void updateHelperData(List<BorrowHelper> data) {
        mHelperAmount.setText(getActivity().getString(R.string.helper_her,data.size()));
        mImageGridAdapter.clear();
        mImageGridAdapter.addAll(data);
        mImageGridAdapter.notifyDataSetChanged();
    }
    private void updateDataWithView(BorrowOut borrowOut){
        Glide.with(this).load(borrowOut.getPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .bitmapTransform(new GlideCircleTransform(this))
                .into(mUserPortrait);
        mPublishTime.setText(this.getString(R.string.borrow_out_time, DateUtil.formatSlash(borrowOut.getConfirmTime())));
        mNeedAmount.setText(this.getString(R.string.RMB,String.valueOf(borrowOut.getMoney())));
        mBorrowTime.setText(this.getString(R.string.day,String.valueOf(borrowOut.getDays())));
        mBorrowInterest.setText(this.getString(R.string.RMB,String.valueOf(borrowOut.getInterest())));
        mOption.setText(borrowOut.getContent());
        String location = borrowOut.getLocation();
        if(location==null){
            location = this.getString(R.string.no_location);
        }
        SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(borrowOut.getUserName(),
                "\n" +location, 0.733f, ContextCompat.getColor(this,R.color.assistText));
        mUserNameLand.setText(attentionSpannableString);
        mEndLineTime.setText(DateUtil.compareTime(borrowOut.getEndlineTime()));
        String[] images = borrowOut.getContentImg().split(",");
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
        Glide.with(this).load(src)
                .placeholder(R.drawable.help)
                .into(image);
    }
    @OnClick(R.id.userPortrait)
    public void onClick(View view){
        Launcher.with(getActivity(),UserDataActivity.class).putExtra(Launcher.USER_ID,mBorrowOut.getUserId()).execute();
    }
}
