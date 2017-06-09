package com.sbai.finance.activity.mutual;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.economiccircle.GoodHeartPeopleActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.GoodHeartPeople;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private boolean mStatusChange = false;
    private int mMax;
    private BorrowMine mBorrowMine;
    private MessageAdapter mMessageAdapter;
    private KeyBoardHelper mKeyBoardHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_mine_details);
        ButterKnife.bind(this);
        mBorrowMine = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        initView();
        calculateAvatarNum(this);
        updateBorrowDetails();
     //   requestBorrowMoneyDetails();
        requestGoodHeartPeopleList();
        requestMessageList();
    }

    private void initView() {
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
            if (mBorrowMine.getStatus()== BorrowMine.STATUS_GIVE_HELP
              ||mBorrowMine.getStatus()== BorrowMine.STATUS_NO_CHECKED
              ||mBorrowMine.getStatus()== BorrowMine.STATUS_NO_CHECKED){

                mBorrowStatus.setVisibility(View.VISIBLE);
            }
        }
    };

//    private void requestBorrowMoneyDetails() {
//        Client.getBorrowMoneyDetail(mDataId).setTag(TAG).setIndeterminate(this)
//                .setCallback(new Callback2D<Resp<BorrowMoneyDetails>, BorrowMoneyDetails>() {
//                    @Override
//                    protected void onRespSuccessData(BorrowMoneyDetails borrowMoneyDetails) {
//                        updateBorrowDetails(borrowMoneyDetails);
//                    }
//                }).fire();
//    }

    private void requestGoodHeartPeopleList() {
        Client.getGoodHeartPeopleList(mBorrowMine.getId()).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<GoodHeartPeople>>, List<GoodHeartPeople>>() {
                    @Override
                    protected void onRespSuccessData(List<GoodHeartPeople> goodHeartPeopleList) {
                        updateGoodHeartPeopleList(goodHeartPeopleList);
                    }
                }).fire();
    }
    private void requestMessageList(){
        Client.getBorrowMessage(mBorrowMine.getId()).setTag(TAG).setIndeterminate(this)
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
         Client.sendBorrowMessage(mBorrowMine.getId(),content).setTag(TAG)
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
    private void requestCancelBorrow(Integer id) {
        Client.cancelBorrowIn(id).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            mStatus.setText(getActivity().getString(R.string.end));
                            mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                            mWriteMessage.setVisibility(View.GONE);
                            mBorrowStatus.setVisibility(View.GONE);

                            mBorrowMine.setStatus(BorrowMine.STATUS_END_CANCEL);
                            mStatusChange  = true;
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fire();
    }
    private void requestRepay(int id){
        Client.repayed(id).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()){
                            mStatus.setText(getActivity().getString(R.string.end));
                            mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                            mBorrowStatus.setVisibility(View.GONE);
                            mBorrowMine.setStatus(BorrowMine.STATUS_END_REPAY);
                            mStatusChange = true;
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
                                .putExtra(Launcher.EX_PAYLOAD, mBorrowMine.getId())
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
                                .putExtra(Launcher.EX_PAYLOAD, mBorrowMine.getId())
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
                                .putExtra(Launcher.EX_PAYLOAD, mBorrowMine.getId())
                                .putExtra(Launcher.USER_ID, mBorrowMine.getUserId())
                                .execute();
                    }
                });
            }
        }
    }

    private void updateBorrowDetails() {
        if (null == mBorrowMine) return;
        Glide.with(getActivity()).load(mBorrowMine.getPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(getActivity()))
                .into(mAvatar);

        mUserName.setText(mBorrowMine.getUserName());
        mPublishTime.setText(DateUtil.getFormatTime(mBorrowMine.getCreateDate()));

        if (TextUtils.isEmpty(mBorrowMine.getLocation())) {
            mLocation.setText(R.string.no_location_information);
        } else {
            mLocation.setText(mBorrowMine.getLocation());
        }

        if (mBorrowMine.getIsAttention() == 2) {
            mIsAttention.setText(R.string.is_attention);
        } else {
            mIsAttention.setText("");
        }

        mBorrowMoneyContent.setText(mBorrowMine.getContent());
        mNeedAmount.setText(getActivity().getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(mBorrowMine.getMoney())));
        mBorrowDeadline.setText(getActivity().getString(R.string.day, FinanceUtil.formatWithScaleNoZero(mBorrowMine.getDays())));
        mBorrowInterest.setText(getActivity().getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(mBorrowMine.getInterest())));

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), UserDataActivity.class)
                            .putExtra(Launcher.USER_ID, mBorrowMine.getUserId())
                            .executeForResult(REQ_CODE_USERDATA);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        if (!TextUtils.isEmpty(mBorrowMine.getContentImg())) {
            String[] images = mBorrowMine.getContentImg().split(",");
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

        boolean isSelf = mBorrowMine.getUserId() == LocalUser.getUser().getUserInfo().getId();
        mUserName.setText(mBorrowMine.getUserName());
        switch (mBorrowMine.getStatus()){
            case BorrowMine.STASTU_END_NO_HELP:
            case BorrowMine.STATUS_END_CANCEL:
            case BorrowMine.STATUS_END_NO_ALLOW:
            case BorrowMine.STATUS_END_NO_CHOICE_HELP:
            case BorrowMine.STATUS_END_REPAY:
            case  BorrowMine.STATUS_END_FIIL:
                mStatus.setText(getActivity().getString(R.string.end));
                mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.luckyText));
                mBorrowStatus.setVisibility(View.GONE);
                mWriteMessage.setVisibility(View.GONE);
                break;
            case BorrowMine.STATUS_GIVE_HELP:
            case BorrowMine.STATUS_NO_CHECKED:
                mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                if (isSelf){
                    mStatus.setText(getActivity().getString(R.string.wait_help));
                    mCancel.setEnabled(true);
                    mCancel.setText(getString(R.string.cancel_borrow_in));
                }else{
                    mCancel.setEnabled(false);
                    mCancel.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unluckyText));
                    mCancel.setTextColor( Color.WHITE);
                    mCancel.setText(getString(R.string.commit));
                }
                mBorrowStatus.setVisibility(View.VISIBLE);
                mBorrowOutSuccess.setVisibility(View.GONE);
                mCallOnly.setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                break;
            case BorrowMine.STATUS_INTENTION:
                mBorrowStatus.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.GONE);
                mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                if (isSelf){
                    mStatus.setText(getActivity().getString(R.string.borrow_in_days,mBorrowMine.getConfirmDays()));
                    mCallOnly.setVisibility(View.VISIBLE);
                    mBorrowOutSuccess.setVisibility(View.GONE);
                }else {
                    mStatus.setText(getActivity().getString(R.string.borrow_out_days,mBorrowMine.getConfirmDays()));
                    mCallOnly.setVisibility(View.GONE);
                    mBorrowOutSuccess.setVisibility(View.VISIBLE);
                }
                break;
            case BorrowMine.STATUS_INTENTION_OVER_TIME:
                mBorrowStatus.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.GONE);
                mStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.redAssist));
                mStatus.setText(getActivity().getString(R.string.over_time));
                if (isSelf){
                    mCallOnly.setVisibility(View.VISIBLE);
                    mBorrowOutSuccess.setVisibility(View.GONE);
                }else {
                    mCallOnly.setVisibility(View.GONE);
                    mBorrowOutSuccess.setVisibility(View.VISIBLE);
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

    @OnClick({R.id.writeMessage, R.id.call, R.id.callOnly, R.id.alreadyRepay, R.id.cancel,R.id.send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call:
            case R.id.callOnly:
                requestPhone(mBorrowMine.getId());
                break;
            case R.id.cancel:
                SmartDialog.with(getActivity(), getString(R.string.cancel_confirm))
                        .setMessageTextSize(15)
                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                requestCancelBorrow(mBorrowMine.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegative(R.string.cancel)
                        .show();
                break;
            case R.id.alreadyRepay:
                requestRepay(mBorrowMine.getId());
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

        }
    }

    @Override
    public void onBackPressed() {
        if (mStatusChange){
            Intent intent = new Intent();
            intent.putExtra(Launcher.EX_PAYLOAD,mBorrowMine);
            setResult(RESULT_OK,intent);
        }
        super.onBackPressed();
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
                        " :"+item.getContent(),1.0f, ContextCompat.getColor(context, R.color.blackAssist));
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
                WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
                        (WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);
                if (whetherAttentionShieldOrNot != null) {
                    if (whetherAttentionShieldOrNot.isFollow()) {
                        mIsAttention.setText(R.string.is_attention);
                        mBorrowMine.setIsAttention(2);
                    } else {
                        mIsAttention.setText("");
                        mBorrowMine.setIsAttention(1);
                    }
                    mStatusChange = true;;
                }
            }
        }
    }
}
