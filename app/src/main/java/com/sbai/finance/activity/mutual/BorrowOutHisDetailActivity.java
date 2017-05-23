package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.economiccircle.WantHelpHimOrYouActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.economiccircle.WantHelpHimOrYou;
import com.sbai.finance.model.mutual.BorrowDetails;
import com.sbai.finance.model.mutual.BorrowHelper;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.MyGridView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowOutHisDetailActivity extends BaseActivity {
    public static final String ID="id";
    public static final String CONFIRM_TIME="confirmTime";
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
    @BindView(R.id.more)
    ImageView mMore;
    private int mMax;
    private long mConfirmTime;
    private ImageGridAdapter mImageGridAdapter;
    private BorrowDetails mBorrowDetails;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_out_mine_his_details);
        ButterKnife.bind(this);
        initView();
        mConfirmTime = getIntent().getLongExtra(CONFIRM_TIME,-1);
        if (mConfirmTime==-1){
            mConfirmTime = System.currentTimeMillis();
        }
        int id= getIntent().getIntExtra(ID,-1);
        if (id!=-1){
            requestBorrowDetail(id);
            requestHelper(id);
        }
    }
    private void initView() {
        calculateAvatarNum(this);
        mImageGridAdapter = new ImageGridAdapter(this);
        mGridView.setAdapter(mImageGridAdapter);
        mGridView.setFocusable(false);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launcherWantHelpHer(mBorrowDetails.getId());
            }
        });
    }
    private void calculateAvatarNum(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        float margin =  Display.dp2Px(26, getResources());
        float horizontalSpacing = Display.dp2Px(5, getResources());
        float avatarWidth =  Display.dp2Px(32, getResources());
        float more =  Display.dp2Px(18, getResources());
        mMax = (int) ((screenWidth - margin - more + horizontalSpacing) / (horizontalSpacing + avatarWidth));
    }
    private void launcherWantHelpHer(int loadId){
        Launcher.with(getActivity(), WantHelpHimOrYouActivity.class)
                .putExtra(Launcher.EX_PAYLOAD,loadId)
                .execute();
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
        mBorrowDetails =data;
        Glide.with(this).load(data.getPortrait())
                .bitmapTransform(new GlideCircleTransform(this))
                .placeholder(R.drawable.ic_default_avatar).into(mUserPortrait);

        String location =data.getLocation();
        if(TextUtils.isEmpty(location)){
            location = this.getString(R.string.no_location);
        }
        SpannableString attentionSpannableString;
        if (data.getIsAttention() == BorrowDetails.ATTENTION){
            attentionSpannableString = StrUtil.mergeTextWithRatioColor(data.getUserName(),
                    getString(R.string.is_attention), "\n" +location, 0.733f, 0.733f,
                    ContextCompat.getColor(this,R.color.assistText),ContextCompat.getColor(this,R.color.assistText));
        }else{
            attentionSpannableString = StrUtil.mergeTextWithRatioColor(data.getUserName(),
                    "\n" +location, 0.733f,ContextCompat.getColor(this,R.color.assistText));
        }
        mUserNameLand.setText(attentionSpannableString);
        mPublishTime.setText(this.getString(R.string.borrow_out_time, DateUtil.getFormatTime(data.getConfirmTime())));
        mNeedAmount.setText(getActivity().getString(R.string.RMB,String.valueOf(data.getMoney())));
        mBorrowTime.setText(getActivity().getString(R.string.day,String.valueOf(data.getDays())));
        mBorrowInterest.setText(getActivity().getString(R.string.RMB,String.valueOf(data.getInterest())));
        if (data.getSex()==BorrowDetails.WOMEN){
            mHelperAmount.setText(getActivity().getString(R.string.helper_her,data.getIntentionCount()));
        }else{
            mHelperAmount.setText(getActivity().getString(R.string.helper_his,data.getIntentionCount()));
        }

        mOption.setText(data.getContent());
        if (data.getContentImg()==null){
            data.setContentImg("");
        }
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
    private void launcherImageView(int index){
        Launcher.with(getActivity(), ContentImgActivity.class)
                .putExtra(Launcher.EX_PAYLOAD,mBorrowDetails.getContentImg().split(","))
                .putExtra(Launcher.EX_PAYLOAD_1,index)
                .execute();
    }
    @OnClick({R.id.userPortrait,R.id.image1,R.id.image2,R.id.image3,R.id.image4})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.userPortrait:
                Launcher.with(getActivity(),UserDataActivity.class).putExtra(Launcher.USER_ID,mBorrowDetails.getUserId()).execute();
                break;
            case R.id.image1:
                launcherImageView(0);
                break;
            case R.id.image2:
                launcherImageView(1);
                break;
            case R.id.image3:
                launcherImageView(2);
                break;
            case R.id.image4:
                launcherImageView(3);
                break;
            case R.id.more:
                launcherWantHelpHer(mBorrowDetails.getId());
                break;
            default:
                break;
        }
    }
    private void loadImage(String src,ImageView image){
        Glide.with(this).load(src).placeholder(R.drawable.ic_loading_pic).into(image);
    }
    private void updateHelperData(List<BorrowHelper> data) {
        mImageGridAdapter.clear();
        if (data.size()>mMax){
            mImageGridAdapter.addAll(data.subList(0,mMax));
            mMore.setVisibility(View.VISIBLE);
        }else{
            mImageGridAdapter.addAll(data);
            mMore.setVisibility(View.GONE);
        }
        mImageGridAdapter.notifyDataSetChanged();
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