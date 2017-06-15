package com.sbai.finance.activity.mutual;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.economiccircle.GoodHeartPeopleActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.GoodHeartPeople;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.model.mutual.BorrowDetail;
import com.sbai.finance.model.mutual.BorrowMessage;
import com.sbai.finance.model.mutual.BorrowMine;
import com.sbai.finance.model.mutual.CallPhone;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.KeyBoardHelper;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BorrowDetailsActivity extends BaseActivity {
    private static final int REQ_BORROW_MONEY_DETAILS = 1001;
    private static final int REQ_WANT_HELP_HIM_OR_YOU = 1002;
    public static final String STATUS_CHANAGE="xxx";
    public static final String DATA_ID="id";
    public static final String DATA_STATUS="status";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
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
    @BindView(R.id.listView)
    MyListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.call)
    TextView mCall;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.alreadyRepay)
    TextView mAlreadyRepay;
    @BindView(R.id.borrowOutSuccess)
    LinearLayout mBorrowOutSuccess;
    @BindView(R.id.callOnly)
    TextView mCallOnly;
    @BindView(R.id.borrowStatus)
    LinearLayout mBorrowStatus;
    @BindView(R.id.writeMessage)
    TextView mWriteMessage;
    @BindView(R.id.leaveMessage)
    EditText mLeaveMessage;
    @BindView(R.id.send)
    TextView mSend;
    @BindView(R.id.leaveMessageArea)
    LinearLayout mLeaveMessageArea;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.giveHelp)
    TextView mGiveHelp;
    private int mMax;
    private BorrowDetail mBorrowDetail;
    private int mLoadId;
    private int mReturnHome;
    private MessageAdapter mMessageAdapter;
    private KeyBoardHelper mKeyBoardHelper;
    private AttentionAndFansNumberModel mAttentionAndFansNumberModel;
    private WhetherAttentionShieldOrNot mWhetherAttentionShieldOrNot;
    private LocalBroadcastManager mLocalBroadcastManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_details);
        ButterKnife.bind(this);
        mLoadId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        mReturnHome=getIntent().getIntExtra(Launcher.EX_PAYLOAD_1,-1);
        initView();
        calculateAvatarNum(this);
        requestBorrowMoneyDetails();
        requestGoodHeartPeopleList();
        requestMessageList();
    }

    private void initView() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mTitleBar.setBackClickListener(new TitleBar.OnBackClickListener() {
            @Override
            public void onClick() {
                if (mReturnHome!=-1){
                   Launcher.with(getActivity(), MainActivity.class).execute();
                }
            }
        });
        setKeyboardHelper();
        mListView.setFocusable(false);
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setCallback(new MessageAdapter.Callback() {
            @Override
            public void onUserClick(int userId) {
                Launcher.with(getActivity(), UserDataActivity.class)
                        .putExtra(Launcher.USER_ID, userId)
                        .executeForResult(REQ_CODE_USERDATA);
            }
        });
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mMessageAdapter);
    }
    private void setKeyboardHelper() {
        mKeyBoardHelper = new KeyBoardHelper(this);
        mKeyBoardHelper.onCreate();
        mKeyBoardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
    }

    private void calculateAvatarNum(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int margin = (int) Display.dp2Px(68, getResources());
        int horizontalSpacing = (int) Display.dp2Px(10, getResources());
        int avatarWidth = (int) Display.dp2Px(32, getResources());
        mMax = (screenWidth - margin + horizontalSpacing) / (horizontalSpacing + avatarWidth);
    }

    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener(){

        @Override
        public void OnKeyBoardPop(int keyboardHeight) {

        }

        @Override
        public void OnKeyBoardClose(int oldKeyboardHeight) {
            mLeaveMessageArea.setVisibility(View.GONE);
            if (mBorrowDetail.getStatus()== BorrowMine.STATUS_GIVE_HELP
              ||mBorrowDetail.getStatus()== BorrowMine.STATUS_NO_CHECKED
              ||mBorrowDetail.getStatus()== BorrowMine.STATUS_NO_CHECKED){

                mBorrowStatus.setVisibility(View.VISIBLE);
            }
        }
    };

private void requestBorrowMoneyDetails() {
    Client.getBorrowMoneyDetail(mLoadId).setTag(TAG).setIndeterminate(this)
            .setCallback(new Callback2D<Resp<BorrowDetail>, BorrowDetail>() {
                @Override
                protected void onRespSuccessData(BorrowDetail borrowDetail) {
                    updateBorrowDetails( borrowDetail);
                }
            }).fire();
}
    private void requestGoodHeartPeopleList() {
        Client.getGoodHeartPeopleList(mLoadId).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<GoodHeartPeople>>, List<GoodHeartPeople>>() {
                    @Override
                    protected void onRespSuccessData(List<GoodHeartPeople> goodHeartPeopleList) {
                        updateGoodHeartPeopleList(goodHeartPeopleList);
                    }
                }).fire();
    }
    private void requestMessageList(){
        Client.getBorrowMessage(mLoadId).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<BorrowMessage>>,List<BorrowMessage>>() {
                    @Override
                    protected void onRespSuccessData(List<BorrowMessage> data) {
                        updateMessageList(data);
                    }
                }).fire();
    }
    private void requestSendMessage(){
        String content = mLeaveMessage.getText().toString();
        if (content.length()>=100){
            content = content.substring(0,100);
        }
         Client.sendBorrowMessage(mBorrowDetail.getId(),content).setTag(TAG)
                 .setCallback(new Callback<Resp<Object>>() {
                     @Override
                     protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()){
                            requestMessageList();
                        }else {
                            ToastUtil.show(resp.getMsg());
                        }
                     }
                 }).fireSync();
    }
    private void requestCancelBorrow(final Integer id) {
        Client.cancelBorrowIn(id).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            mStatus.setText(getActivity().getString(R.string.end));
                            mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                            mWriteMessage.setVisibility(View.GONE);
                            mBorrowStatus.setVisibility(View.GONE);
                            mBorrowDetail.setStatus(BorrowMine.STATUS_END_CANCEL);
                            sendStatusChangeBroadCast(BorrowDetail.STATUS_END_CANCEL,id);
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fire();
    }
    private void requestRepay(final int id){
        Client.repayed(id).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()){
                            mStatus.setText(getActivity().getString(R.string.end));
                            mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                            mBorrowStatus.setVisibility(View.GONE);
                            mBorrowDetail.setStatus(BorrowMine.STATUS_END_REPAY);
                            sendStatusChangeBroadCast(BorrowDetail.STATUS_END_REPAY,id);
                        }else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fireSync();
    }
    private void requestPhone(int id){
        Client.getPhone(id).setTag(TAG)
                .setCallback(new Callback<Resp<CallPhone>>() {
                    @Override
                    protected void onRespSuccess(Resp<CallPhone> resp) {
                        if (resp.isSuccess()){
                            callPhone(resp.getData());
                        }else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fireSync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKeyBoardHelper.onDestroy();
    }

    private void updateMessageList(List<BorrowMessage> data) {
        mLeaveMessageNum.setText(getString(R.string.leave_message_number,data.size()));
        mMessageAdapter.clear();
        mMessageAdapter.addAll(data);
        mMessageAdapter.notifyDataSetChanged();
    }

    private void updateGoodHeartPeopleList(List<GoodHeartPeople> data) {
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
                        Launcher.with(getActivity(), GoodHeartPeopleActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mBorrowDetail.getId())
//                                .putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
//                                .putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
                                .execute();
                    }
                });

                mMore.setVisibility(View.VISIBLE);
                mMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(getActivity(), GoodHeartPeopleActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mBorrowDetail.getId())
//                                .putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
//                                .putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
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
                        Launcher.with(getActivity(), GoodHeartPeopleActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mBorrowDetail.getId())
                                .putExtra(Launcher.EX_PAYLOAD_1,mBorrowDetail.getStatus())
                                .putExtra(Launcher.USER_ID, mBorrowDetail.getUserId())
                                .execute();
                    }
                });
            }
        }
    }

    private void updateBorrowDetails(BorrowDetail borrowDetail) {
        if (null == borrowDetail) return;
        mBorrowDetail = borrowDetail;
        Glide.with(getActivity()).load(borrowDetail.getPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(getActivity()))
                .into(mAvatar);

        mUserName.setText(borrowDetail.getUserName());
        mPublishTime.setText(DateUtil.getFormatTime(borrowDetail.getCreateDate()));

        if (TextUtils.isEmpty(borrowDetail.getLocation())) {
            mLocation.setText(R.string.no_location_information);
        } else {
            mLocation.setText(borrowDetail.getLocation());
        }

        if (borrowDetail.getIsAttention() == BorrowDetail.ATTENTION) {
            mIsAttention.setText(R.string.is_attention);
        } else {
            mIsAttention.setText("");
        }

        mBorrowMoneyContent.setText(borrowDetail.getContent());
        mNeedAmount.setText(getActivity().getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(borrowDetail.getMoney())));
        mBorrowDeadline.setText(getActivity().getString(R.string.day, FinanceUtil.formatWithScaleNoZero(borrowDetail.getDays())));
        mBorrowInterest.setText(getActivity().getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(borrowDetail.getInterest())));

        if (!TextUtils.isEmpty(borrowDetail.getContentImg())) {
            String[] images = borrowDetail.getContentImg().split(",");
            switch (images.length) {
                case 1:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage(images[0], mImage1);
                    mImage2.setVisibility(View.INVISIBLE);
                    mImage3.setVisibility(View.INVISIBLE);
                    mImage4.setVisibility(View.INVISIBLE);
                    imageClick(images, mImage1, 0);
                    break;
                case 2:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage(images[0], mImage1);
                    mImage2.setVisibility(View.VISIBLE);
                    loadImage(images[1], mImage2);
                    mImage3.setVisibility(View.INVISIBLE);
                    mImage4.setVisibility(View.INVISIBLE);
                    imageClick(images, mImage1, 0);
                    imageClick(images, mImage2, 1);
                    break;
                case 3:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage(images[0], mImage1);
                    mImage2.setVisibility(View.VISIBLE);
                    loadImage(images[1], mImage2);
                    mImage3.setVisibility(View.VISIBLE);
                    loadImage(images[2], mImage3);
                    mImage4.setVisibility(View.INVISIBLE);
                    imageClick(images, mImage1, 0);
                    imageClick(images, mImage2, 1);
                    imageClick(images, mImage3, 2);
                    break;
                case 4:
                    mContentImg.setVisibility(View.VISIBLE);
                    mImage1.setVisibility(View.VISIBLE);
                    loadImage(images[0], mImage1);
                    mImage2.setVisibility(View.VISIBLE);
                    loadImage(images[1], mImage2);
                    mImage3.setVisibility(View.VISIBLE);
                    loadImage(images[2], mImage3);
                    mImage4.setVisibility(View.VISIBLE);
                    loadImage(images[3], mImage4);
                    imageClick(images, mImage1, 0);
                    imageClick(images, mImage2, 1);
                    imageClick(images, mImage3, 2);
                    imageClick(images, mImage4, 3);
                    break;
                default:
                    break;
            }
        } else {
            mContentImg.setVisibility(View.GONE);
        }
        mGiveHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), LoginActivity.class).executeForResult(REQ_BORROW_MONEY_DETAILS);
                } else {
                    SmartDialog.with(getActivity(),
                            getString(R.string.give_help_dialog_content)
                            , getString(R.string.give_help_dialog_title, mBorrowDetail.getUserName()))
                            .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog) {
                                    Client.giveHelp(mLoadId).setTag(TAG).
                                            setIndeterminate(BorrowDetailsActivity.this)
                                            .setCallback(new Callback<Resp<JsonPrimitive>>() {

                                                @Override
                                                protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                                                    if (resp.isSuccess()) {
                                                        requestBorrowMoneyDetails();
                                                        requestGoodHeartPeopleList();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(VolleyError volleyError) {
                                                    super.onFailure(volleyError);
                                                    //处理多端问题
                                                    requestBorrowMoneyDetails();
                                                    requestGoodHeartPeopleList();
                                                }
                                            }).fire();

                                    dialog.dismiss();
                                }
                            })
                            .setTitleMaxLines(2)
                            .setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.blackAssist))
                            .setMessageTextColor(ContextCompat.getColor(getActivity(), R.color.opinionText))
                            .setNegative(R.string.cancel)
                            .show();
                }
            }
        });
        mUserName.setText(borrowDetail.getUserName());
        boolean isSelfLoadIn = borrowDetail.getUserId() == LocalUser.getUser().getUserInfo().getId();
        boolean isSelfLoadOut = borrowDetail.getSelectedUserId() == LocalUser.getUser().getUserInfo().getId();
        switch (borrowDetail.getStatus()){
            case BorrowDetail.STASTU_END_NO_HELP:
            case BorrowDetail.STATUS_END_CANCEL:
            case BorrowDetail.STATUS_END_NO_ALLOW:
            case BorrowDetail.STATUS_END_NO_CHOICE_HELP:
            case BorrowDetail.STATUS_END_REPAY:
            case  BorrowDetail.STATUS_END_FIIL:
                mStatus.setText(getActivity().getString(R.string.end));
                mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                mBorrowStatus.setVisibility(View.GONE);
                mWriteMessage.setVisibility(View.GONE);
                break;
            case BorrowDetail.STATUS_GIVE_HELP:
            case BorrowDetail.STATUS_NO_CHECKED:
            case BorrowDetail.STATUS_ACCEPTY:
                mBorrowStatus.setVisibility(View.VISIBLE);
                mBorrowOutSuccess.setVisibility(View.GONE);
                mCallOnly.setVisibility(View.GONE);
                mWriteMessage.setVisibility(View.VISIBLE);
                mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                mStatus.setText(getActivity().getString(R.string.wait_help));
                if (isSelfLoadIn){
                    mCancel.setVisibility(View.VISIBLE);
                    mGiveHelp.setVisibility(View.GONE);
                    mCancel.setText(getString(R.string.cancel_borrow_in));
                }else{
                    mCancel.setVisibility(View.GONE);
                    mGiveHelp.setVisibility(View.VISIBLE);
                    if (borrowDetail.getIsIntention() == BorrowDetail.INTENTIONED) {
                        mGiveHelp.setText(R.string.submitted);
                        mGiveHelp.setEnabled(false);
                    } else {
                        mGiveHelp.setText(R.string.give_help);
                        mGiveHelp.setEnabled(true);
                    }

                }
                break;
            case BorrowDetail.STATUS_INTENTION:
                mBorrowStatus.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.GONE);
                mGiveHelp.setVisibility(View.GONE);
                mWriteMessage.setVisibility(View.GONE);
                if (isSelfLoadIn){
                    mStatus.setText(getActivity().getString(R.string.borrow_in_days,borrowDetail.getConfirmDays()));
                    mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                    mCallOnly.setVisibility(View.VISIBLE);
                    mBorrowOutSuccess.setVisibility(View.GONE);
                }else if (isSelfLoadOut){
                    mStatus.setText(getActivity().getString(R.string.borrow_out_days,borrowDetail.getConfirmDays()));
                    mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                    mCallOnly.setVisibility(View.GONE);
                    mBorrowOutSuccess.setVisibility(View.VISIBLE);
                }else{
                    mStatus.setText(getActivity().getString(R.string.end));
                    mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                    mCallOnly.setVisibility(View.GONE);
                    mBorrowOutSuccess.setVisibility(View.GONE);
                }
                break;
            case BorrowDetail.STATUS_INTENTION_OVER_TIME:
                mBorrowStatus.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.GONE);
                mGiveHelp.setVisibility(View.GONE);
                mWriteMessage.setVisibility(View.GONE);
                if (isSelfLoadIn){
                    mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                    mStatus.setText(getActivity().getString(R.string.over_time));
                    mCallOnly.setVisibility(View.VISIBLE);
                    mBorrowOutSuccess.setVisibility(View.GONE);
                }else if (isSelfLoadOut){
                    mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                    mStatus.setText(getActivity().getString(R.string.over_time));
                    mCallOnly.setVisibility(View.GONE);
                    mBorrowOutSuccess.setVisibility(View.VISIBLE);
                }else{
                    mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                    mStatus.setText(getActivity().getString(R.string.end));
                    mCallOnly.setVisibility(View.GONE);
                    mBorrowOutSuccess.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void loadImage(String src, ImageView image) {
        Glide.with(getActivity())
                .load(src)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.logo_login)
                .into(image);
    }

    private void imageClick(final String[] images,
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
    @OnClick({R.id.writeMessage, R.id.call, R.id.callOnly, R.id.alreadyRepay, R.id.cancel,R.id.send,R.id.titleBar,R.id.userName,R.id.avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userName:
            case R.id.avatar:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), UserDataActivity.class)
                            .putExtra(Launcher.USER_ID, mBorrowDetail.getUserId())
                            .executeForResult(REQ_CODE_USERDATA);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.call:
            case R.id.callOnly:
                requestPhone(mBorrowDetail.getId());
                break;
            case R.id.cancel:
                SmartDialog.with(getActivity(), getString(R.string.cancel_confirm))
                        .setMessageTextSize(15)
                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                requestCancelBorrow(mBorrowDetail.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegative(R.string.cancel)
                        .show();
                break;
            case R.id.alreadyRepay:
                SmartDialog.with(getActivity(), getString(R.string.repay_confirm))
                        .setMessageTextSize(15)
                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                requestRepay(mBorrowDetail.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegative(R.string.cancel)
                        .show();
                break;
            case R.id.writeMessage:
                mLeaveMessageArea.setVisibility(View.VISIBLE);
                mBorrowStatus.setVisibility(View.GONE);
                mLeaveMessage.setText("");
                mLeaveMessage.requestFocus();
                mLeaveMessage.setFocusable(true);
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.send:
                if (!TextUtils.isEmpty(mLeaveMessage.getText())){
                    requestSendMessage();
                }
                break;
            case R.id.titleBar:
                mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mScrollView.smoothScrollTo(0,0);
                    }
                });
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (mReturnHome!=-1){
            Launcher.with(getActivity(), MainActivity.class).execute();
        }else{
            Intent intent = new Intent();
            intent.putExtra(Launcher.EX_PAYLOAD_1, mWhetherAttentionShieldOrNot);
            intent.putExtra(Launcher.EX_PAYLOAD_2, mAttentionAndFansNumberModel);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
    private void sendStatusChangeBroadCast(int status,int id){
        Intent intent = new Intent();
        intent.setAction(STATUS_CHANAGE);
        intent.putExtra(DATA_STATUS,status);
        intent.putExtra(DATA_ID,id);
        mLocalBroadcastManager.sendBroadcastSync(intent);
    }

    private void callPhone(CallPhone phone){
        if (phone.getSelectedPhone()!=null){
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone.getSelectedPhone()));
            startActivity(intent);
        }else {
            ToastUtil.curt(getString(R.string.no_phone));
        }
    }
    static class MessageAdapter extends ArrayAdapter<BorrowMessage> {
        interface Callback{
            void onUserClick(int userId);
        }
        private Callback mCallback;
        public void setCallback(Callback callback){
            mCallback = callback;
        }
        public MessageAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position),getContext(), mCallback);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.message)
            TextView mMessage;
            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
            private void bindDataWithView(final BorrowMessage item, Context context, final Callback callback){
                SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(item.getUserName(),
                        ": "+item.getContent(),1.0f, ContextCompat.getColor(context, R.color.blackAssist));
                attentionSpannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                         callback.onUserClick(item.getUserId());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                },0,item.getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mMessage.setText(attentionSpannableString);
                mMessage.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_USERDATA && resultCode == RESULT_OK) {
            if (data != null) {
                mWhetherAttentionShieldOrNot=
                        (WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);
                mAttentionAndFansNumberModel  =
                        (AttentionAndFansNumberModel) data.getSerializableExtra(Launcher.EX_PAYLOAD_2);
                if (mWhetherAttentionShieldOrNot != null) {
                    if (mWhetherAttentionShieldOrNot.isFollow()) {
                        mIsAttention.setText(R.string.is_attention);
                        mBorrowDetail.setIsAttention(BorrowDetail.ATTENTION);
                    } else {
                        mIsAttention.setText("");
                        mBorrowDetail.setIsAttention(BorrowDetail.NO_ATTENTION);
                    }
                    if (mWhetherAttentionShieldOrNot.isShield()){
                        requestMessageList();
                    }
                }

            }
        }
    }
}