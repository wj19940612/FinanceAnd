package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.economiccircle.WantHelpHimOrYouActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.BorrowMoneyDetails;
import com.sbai.finance.model.economiccircle.WantHelpHimOrYou;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.MyListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
public class BorrowMineDetailsActivity extends BaseActivity {
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.isAttention)
    TextView mIsAttention;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.borrowMoneyContent)
    TextView mBorrowMoneyContent;
    @BindView(R.id.needAmount)
    TextView mNeedAmount;
    @BindView(R.id.borrowDeadline)
    TextView mBorrowDeadline;
    @BindView(R.id.borrowInterest)
    TextView mBorrowInterest;
    @BindView(R.id.image1)
    ImageView mImage1;
    @BindView(R.id.image2)
    ImageView mImage2;
    @BindView(R.id.image3)
    ImageView mImage3;
    @BindView(R.id.image4)
    ImageView mImage4;
    @BindView(R.id.contentImg)
    LinearLayout mContentImg;
    @BindView(R.id.publishTime)
    TextView mPublishTime;
    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.avatarList)
    LinearLayout mAvatarList;
    @BindView(R.id.more)
    ImageView mMore;
    @BindView(R.id.goodHeartPeopleArea)
    RelativeLayout mGoodHeartPeopleArea;
    @BindView(R.id.leaveMessageNum)
    TextView mLeaveMessageNum;
    @BindView(android.R.id.list)
    MyListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.call)
    TextView mCall;
    @BindView(R.id.alreadyRepay)
    TextView mAlreadyRepay;
    @BindView(R.id.borrowOutSuccess)
    LinearLayout mBorrowOutSuccess;
    @BindView(R.id.callOnly)
    TextView mCallOnly;
    @BindView(R.id.borrowStatus)
    LinearLayout mBorrowStatus;
    private int mDataId;
    private int mMax;
    private BorrowMoneyDetails mBorrowMoneyDetails;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_mine_details);
        ButterKnife.bind(this);
        mDataId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        calculateAvatarNum(this);
        requestBorrowMoneyDetails();
        requestWantHelpHimList();
    }
    private void calculateAvatarNum(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int margin = (int) Display.dp2Px(68, getResources());
        int horizontalSpacing = (int) Display.dp2Px(10, getResources());
        int avatarWidth = (int) Display.dp2Px(32, getResources());
        mMax = (screenWidth - margin + horizontalSpacing) / (horizontalSpacing + avatarWidth);
    }
    private void requestBorrowMoneyDetails() {
        Client.getBorrowMoneyDetail(mDataId).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<BorrowMoneyDetails>, BorrowMoneyDetails>() {
                    @Override
                    protected void onRespSuccessData(BorrowMoneyDetails borrowMoneyDetails) {
                        updateBorrowDetails(borrowMoneyDetails);
                    }
                }).fire();
    }
    private void requestWantHelpHimList() {
        Client.getWantHelpHimOrYouList(mDataId).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<WantHelpHimOrYou>>, List<WantHelpHimOrYou>>() {
                    @Override
                    protected void onRespSuccessData(List<WantHelpHimOrYou> wantHelpHimOrYouList) {
                        updateWantHelpHimList(wantHelpHimOrYouList);
                    }
                }).fire();
    }

    private void updateWantHelpHimList(List<WantHelpHimOrYou> data) {
        int width = (int) Display.dp2Px(32, getResources());
        int height = (int) Display.dp2Px(32, getResources());
        int margin = (int) Display.dp2Px(10, getResources());

        mAvatarList.removeAllViews();

        int size = data.size();
        if (size > 0) {
            mGoodHeartPeopleArea.setVisibility(View.VISIBLE);
        }
        if (size >= mMax) {
            size = mMax;
            for (int i = 0; i < size - 1; i++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = (i == 0 ? 0 : margin);
                imageView.setLayoutParams(params);
                Glide.with(this).load(data.get(i).getPortrait())
                        .bitmapTransform(new GlideCircleTransform(this))
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(imageView);
                mAvatarList.addView(imageView);

                mAvatarList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(getActivity(), WantHelpHimOrYouActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mDataId)
                                .putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
                                .putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
                                .execute();
                    }
                });

                mMore.setVisibility(View.VISIBLE);
                mMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(getActivity(), WantHelpHimOrYouActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mDataId)
                                .putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
                                .putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
                                .execute();
                    }
                });
            }
        } else {

            for (int i = 0; i < size; i++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = (i == 0 ? 0 : margin);
                imageView.setLayoutParams(params);
                Glide.with(this).load(data.get(i).getPortrait())
                        .bitmapTransform(new GlideCircleTransform(this))
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(imageView);
                mAvatarList.addView(imageView);

                mMore.setVisibility(View.GONE);
                mAvatarList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(getActivity(), WantHelpHimOrYouActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mDataId)
                                .putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
                                .putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
                                .execute();
                    }
                });
            }
        }
    }

    private void updateBorrowDetails(final BorrowMoneyDetails data) {
        if (null == data) return;
        Glide.with(getActivity()).load(data.getPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(getActivity()))
                .into(mAvatar);

        mUserName.setText(data.getUserName());
        mPublishTime.setText(DateUtil.getFormatTime(data.getAuditTime()));

        if (TextUtils.isEmpty(data.getLocation())) {
            mLocation.setText(R.string.no_location_information);
        } else {
            mLocation.setText(data.getLocation());
        }

        if (data.getIsAttention() == 2) {
            mIsAttention.setText(R.string.is_attention);
        } else {
            mIsAttention.setText("");
        }

        mBorrowMoneyContent.setText(data.getContent());
        mNeedAmount.setText(getActivity().getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(data.getMoney())));
        mBorrowDeadline.setText(getActivity().getString(R.string.day, FinanceUtil.formatWithScaleNoZero(data.getDays())));
        mBorrowInterest.setText(getActivity().getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(data.getInterest())));

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), UserDataActivity.class)
                            .putExtra(Launcher.USER_ID, data.getUserId())
                            .executeForResult(REQ_CODE_USERDATA);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        if (!TextUtils.isEmpty(data.getContentImg())) {
            String[] images = data.getContentImg().split(",");
            switch (images.length) {
                case 1:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage( images[0], mImage1);
                    mImage2.setVisibility(View.INVISIBLE);
                    mImage3.setVisibility(View.INVISIBLE);
                    mImage4.setVisibility(View.INVISIBLE);
                    imageClick( images, mImage1, 0);
                    break;
                case 2:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage( images[0], mImage1);
                    mImage2.setVisibility(View.VISIBLE);
                    loadImage( images[1], mImage2);
                    mImage3.setVisibility(View.INVISIBLE);
                    mImage4.setVisibility(View.INVISIBLE);
                    imageClick( images, mImage1, 0);
                    imageClick( images, mImage2, 1);
                    break;
                case 3:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage( images[0], mImage1);
                    mImage2.setVisibility(View.VISIBLE);
                    loadImage( images[1], mImage2);
                    mImage3.setVisibility(View.VISIBLE);
                    loadImage( images[2], mImage3);
                    mImage4.setVisibility(View.INVISIBLE);
                    imageClick( images, mImage1, 0);
                    imageClick( images, mImage2, 1);
                    imageClick( images, mImage3, 2);
                    break;
                case 4:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage( images[0], mImage1);
                    mImage2.setVisibility(View.VISIBLE);
                    loadImage( images[1], mImage2);
                    mImage3.setVisibility(View.VISIBLE);
                    loadImage( images[2], mImage3);
                    mImage4.setVisibility(View.VISIBLE);
                    loadImage( images[3], mImage4);
                    imageClick( images, mImage1, 0);
                    imageClick( images, mImage2, 1);
                    imageClick( images, mImage3, 2);
                    imageClick( images, mImage4, 3);
                    break;
                default:
                    break;
            }
        } else {
            mContentImg.setVisibility(View.GONE);
        }
    }
    private void loadImage( String src, ImageView image) {
        Glide.with(getActivity())
                .load(src)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.logo_login)
                .into(image);
    }
    private void imageClick( final String[] images,
                            ImageView imageView, final int i) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContentImgActivity.class);
                intent.putExtra(Launcher.EX_PAYLOAD, images);
                intent.putExtra(Launcher.EX_PAYLOAD_1, i);
                getActivity().startActivity(intent);
            }
        });
    }
}
