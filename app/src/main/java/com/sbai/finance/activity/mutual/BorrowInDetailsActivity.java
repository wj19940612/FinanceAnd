package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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


public class BorrowInDetailsActivity extends BaseActivity {
    public static final String BORROW_IN="borrowIn";
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
    @BindView(R.id.gridView)
    MyGridView mGridView;
    private ImageGridAdapter mImageGridAdapter;
    private BorrowIn mBorrowIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine_details);
        ButterKnife.bind(this);
        initView();
        mBorrowIn= getIntent().getParcelableExtra(BORROW_IN);
        if (mBorrowIn!=null){
            initBorrowInData();
        }
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        updateEndLineData();
    }

    private void updateEndLineData() {
        if (mBorrowIn!=null&&mBorrowIn.getStatus()==BorrowIn.STATUS_CHECKED){
            SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(getActivity().getString(R.string.call_helper),
                    "\n" +getActivity().getString(R.string.end_line),"  "+DateUtil.compareTime(mBorrowIn.getEndlineTime()), 1.455f,1.455f,
                    ContextCompat.getColor(getActivity(),R.color.opinionText), ContextCompat.getColor(getActivity(),R.color.redPrimary));
            mEndLineTime.setText(attentionSpannableString);
        }
    }

    private void initView() {
        mImageGridAdapter = new ImageGridAdapter(this);
        mGridView.setAdapter(mImageGridAdapter);
        mGridView.setFocusable(false);
    }

    private void initBorrowInData() {
        mPublishTime.setText(getActivity().getString(R.string.publish_time, DateUtil.formatSlash(mBorrowIn.getCreateDate())));
        mNeedAmount.setText(getActivity().getString(R.string.RMB,String.valueOf(mBorrowIn.getMoney())));
        mBorrowTime.setText(getActivity().getString(R.string.day,String.valueOf(mBorrowIn.getDays())));
        mBorrowInterest.setText(getActivity().getString(R.string.RMB,String.valueOf(mBorrowIn.getInterest())));
        mHelperAmount.setText(getActivity().getString(R.string.helper,mBorrowIn.getIntentionCount()));
        mOption.setText(mBorrowIn.getContent());
        SpannableString attentionSpannableString;
        switch (mBorrowIn.getStatus()){
            case BorrowIn.STATUS_CHECKED:
                attentionSpannableString = StrUtil.mergeTextWithRatioColor(getActivity().getString(R.string.call_helper),
                        "\n" +getActivity().getString(R.string.end_line),"  "+DateUtil.compareTime(mBorrowIn.getEndlineTime()), 1.455f,1.455f,
                        ContextCompat.getColor(getActivity(),R.color.opinionText), ContextCompat.getColor(getActivity(),R.color.redPrimary));
                mEndLineTime.setText(attentionSpannableString);
                mEndLineTime.setGravity(Gravity.LEFT);
                break;
            case BorrowIn.STATUS_NO_CHECK:
                attentionSpannableString = StrUtil.mergeTextWithColor(getActivity().getString(R.string.on_checking),1.455f,
                        ContextCompat.getColor(getActivity(),R.color.opinionText));
                mEndLineTime.setText(attentionSpannableString);
                mEndLineTime.setGravity(Gravity.CENTER);
                break;
            default:
                break;
        }
        String[] images = mBorrowIn.getContentImg().split(",");
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
        requestHelper(mBorrowIn.getId());
        startScheduleJob(60*1000);

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
        mHelperAmount.setText(getActivity().getString(R.string.helper,data.size()));
        mImageGridAdapter.clear();
        mImageGridAdapter.addAll(data);
        mImageGridAdapter.notifyDataSetChanged();
    }
    private void loadImage(String src,ImageView image){
        Glide.with(this).load(src).placeholder(R.drawable.help).into(image);
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
