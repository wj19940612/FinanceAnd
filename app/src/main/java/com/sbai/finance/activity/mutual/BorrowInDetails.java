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
import com.sbai.finance.model.mutual.BorrowIn;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-05-11.
 */

public class BorrowInDetails extends BaseActivity {
    public static final String BORRROW_IN="borrowIn";
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine_details);
        ButterKnife.bind(this);
        BorrowIn borrowIn = getIntent().getParcelableExtra(BORRROW_IN);
        if (borrowIn!=null){
            updateBorrowInData(borrowIn);
        }
    }

    private void updateBorrowInData(BorrowIn borrowIn) {
        mPublishTime.setText(getActivity().getString(R.string.publish_time, DateUtil.formatSlash(borrowIn.getCreateDate())));
        mNeedAmount.setText(getActivity().getString(R.string.RMB,String.valueOf(borrowIn.getMoney())));
        mBorrowTime.setText(getActivity().getString(R.string.day,String.valueOf(borrowIn.getDays())));
        mBorrowInterest.setText(getActivity().getString(R.string.RMB,String.valueOf(borrowIn.getInterest())));
        mHelperAmount.setText(getActivity().getString(R.string.helper,borrowIn.getIntentionCount()));
        mOption.setText(borrowIn.getContent());
        SpannableString attentionSpannableString;
        switch (borrowIn.getStatus()){
            case BorrowIn.STATUS_CHECKED:
                attentionSpannableString = StrUtil.mergeTextWithRatioColor(getActivity().getString(R.string.call_helper),
                        "\n" +getActivity().getString(R.string.end_line),"  "+DateUtil.compareTime(borrowIn.getEndlineTime()), 1.455f,1.455f,
                        ContextCompat.getColor(getActivity(),R.color.opinionText), ContextCompat.getColor(getActivity(),R.color.redPrimary));
                mEndLineTime.setText(attentionSpannableString);
                mCancelBorrowIn.setVisibility(View.VISIBLE);
                break;
            case BorrowIn.STATUS_NO_CHECK:
                attentionSpannableString = StrUtil.mergeTextWithColor(getActivity().getString(R.string.on_checking),1.455f,
                        ContextCompat.getColor(getActivity(),R.color.opinionText));
                mEndLineTime.setText(attentionSpannableString);
                mCancelBorrowIn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        String[] images = borrowIn.getContentImg().split(",");
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
//    @OnClick{R.id.cancelBorrowIn}
//    public void onClick(View id){
//
//    }
}
