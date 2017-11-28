package com.sbai.finance.view.radio;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.MissProfileDetailActivity;
import com.sbai.finance.activity.miss.RadioStationListActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.model.radio.RadioDetails;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.ShrinkTextLayout;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/11/22.
 * 电台详情页电台介绍部分
 */

public class RadioInfoLayout extends LinearLayout {

    @BindView(R.id.radioOwnerAvatar)
    HasLabelImageLayout mRadioOwnerAvatar;
    @BindView(R.id.radioOwnerName)
    TextView mRadioOwnerName;
    @BindView(R.id.radioStartPlaySmall)
    ImageView mRadioStartPlaySmall;
    @BindView(R.id.voiceName)
    TextView mVoiceName;
    @BindView(R.id.voiceIntroduce)
    ShrinkTextLayout mVoiceIntroduce;
    @BindView(R.id.split)
    View mSplit;
    @BindView(R.id.radioCover)
    ImageView mRadioCover;
    @BindView(R.id.radioName)
    TextView mRadioName;
    @BindView(R.id.subscribeStatus)
    TextView mSubscribeStatus;
    private Unbinder mBind;

    private Radio mRadio;
    private TextView mReviewTextView;
    private RadioDetails mRadioDetails;

    public RadioInfoLayout(Context context) {
        this(context, null);
    }

    public RadioInfoLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioInfoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_radio_info, this, true);
        mBind = ButterKnife.bind(this, view);


        int padding = (int) Display.dp2Px(14, getResources());
        mReviewTextView = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, padding, 0, 0);
        mReviewTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_latest, 0, 0, 0);
        mReviewTextView.setPadding(padding, 15, 15, 0);
        mReviewTextView.setBackgroundColor(Color.WHITE);
        mReviewTextView.setCompoundDrawablePadding((int) Display.dp2Px(4, getResources()));
        mReviewTextView.setText(getContext().getString(R.string.comment_number_string, StrFormatter.getFormatCount(0)));
        addView(mReviewTextView, layoutParams);

        mSubscribeStatus.setSelected(true);
        mSubscribeStatus.setText(R.string.subscribe);
    }

    public void setRadio(Radio radio) {
        mRadio = radio;
        mVoiceName.setText(radio.getAudioName());
        mVoiceIntroduce.setContentText(radio.getAudioIntroduction());
        mRadioOwnerAvatar.setAvatar(radio.getUserPortrait(), Question.USER_IDENTITY_HOST);
        mRadioOwnerName.setText(radio.getRadioHostName());
        mRadioName.setText(radio.getRadioName());
        GlideApp.with(getContext())
                .load(radio.getRadioCover())
                .into(mRadioCover);
    }

    public void setRadioDetails(RadioDetails radioDetails) {
        mRadioDetails = radioDetails;
        updateRadioSubscriber();
    }

    public void setReviewNumber(int count) {
        mReviewTextView.setText(getContext().getString(R.string.comment_number_string, StrFormatter.getFormatCount(count)));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBind.unbind();
    }

    @OnClick({R.id.radioOwnerAvatar, R.id.radioOwnerName, R.id.radioStartPlaySmall, R.id.voiceName,
            R.id.radioName, R.id.radioCover, R.id.subscribeStatus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.radioOwnerAvatar:
            case R.id.radioOwnerName:
                if (mRadio != null) {
                    Launcher.with(getContext(), MissProfileDetailActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, mRadio.getRadioHost())
                            .execute();
                }
                break;
            case R.id.radioStartPlaySmall:
                // TODO: 2017/11/22 暂时不处理
                break;
            case R.id.voiceName:

                break;
            case R.id.radioName:
            case R.id.radioCover:
                if (mRadio != null) {
                    if (getContext() instanceof Activity) {
                        Activity activity = (Activity) getContext();
                        if (activity.getCallingActivity() != null &&
                                activity.getCallingActivity().getClassName().equalsIgnoreCase(RadioStationListActivity.class.getName())) {
                            activity.finish();
                        } else {
                            Launcher.with(getContext(), RadioStationListActivity.class)
                                    .putExtra(ExtraKeys.RADIO, mRadio)
                                    .executeForResult(555);
                        }
                    } else {
                        Launcher.with(getContext(), RadioStationListActivity.class)
                                .putExtra(ExtraKeys.RADIO, mRadio)
                                .executeForResult(555);
                    }
                }
                break;
            case R.id.subscribeStatus:
                if (LocalUser.getUser().isLogin()) {
                    collectRadio();
                } else {
                    Launcher.with(getContext(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void collectRadio() {
        if (mRadioDetails != null) {
            Client.collect(String.valueOf(mRadio.getRadioId()), Radio.USER_COLLECT_TYPE_RADIO)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                            ToastUtil.show(resp.getMsg());
                            if (mRadioDetails != null) {
                                if (mRadioDetails.getIsSubscriber() == 1) {
                                    mRadioDetails.setIsSubscriber(0);
                                } else {
                                    mRadioDetails.setIsSubscriber(1);
                                }
                            }
                            updateRadioSubscriber();
                        }
                    })
                    .fireFree();
        }
    }

    private void updateRadioSubscriber() {
        if (mRadioDetails != null) {
            if (mRadioDetails.getIsSubscriber() == 1) {
                mSubscribeStatus.setSelected(false);
                mSubscribeStatus.setText(R.string.already_subscribe);
            } else {
                mSubscribeStatus.setSelected(true);
                mSubscribeStatus.setText(R.string.subscribe);
            }
        }
    }
}
