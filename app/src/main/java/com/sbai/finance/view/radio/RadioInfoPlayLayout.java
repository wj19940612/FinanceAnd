package com.sbai.finance.view.radio;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/11/22.
 * 电台详情页面播放信息
 */

public class RadioInfoPlayLayout extends LinearLayout {
    private static final String TAG = "RadioInfoPlayLayout";

    @BindView(R.id.voiceCover)
    ImageView mVoiceCover;
    @BindView(R.id.progressLength)
    TextView mProgressLength;
    @BindView(R.id.radioSeekBar)
    AppCompatSeekBar mRadioSeekBar;
    @BindView(R.id.radioTotalLength)
    TextView mRadioTotalLength;
    @BindView(R.id.seekBarLL)
    LinearLayout mSeekBarLL;
    @BindView(R.id.play)
    ImageView mPlay;
    @BindView(R.id.listenNumber)
    TextView mListenNumber;
    private Unbinder mBind;
    private Radio mRadio;


    private OnRadioPlayListener mOnRadioPlayListener;
    private Animation mRotateAnimation;

    private int mTotalDuration;

    public interface OnRadioPlayListener {
        void onRadioPlay();

        void onSeekChange(int progress);
    }

    public void setOnRadioPlayListener(OnRadioPlayListener onRadioPlayListener) {
        mOnRadioPlayListener = onRadioPlayListener;
    }

    public RadioInfoPlayLayout(Context context) {
        this(context, null);
    }

    public RadioInfoPlayLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioInfoPlayLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_radio_play, this, true);
        mBind = ButterKnife.bind(this, view);
        createVoicePlayRotateAnimation();
    }

    private void createVoicePlayRotateAnimation() {
        mRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.infinite_rotate);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVoiceCover.clearAnimation();
        mOnSeekBarChangeListener = null;
        mRotateAnimation = null;
        mBind.unbind();
    }

    public void startAnimation() {
        mVoiceCover.startAnimation(mRotateAnimation);
    }

    public void stopAnimation() {
        mVoiceCover.clearAnimation();
    }

    public void setRadioProgress(int progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRadioSeekBar.setProgress(progress, true);
        } else {
            mRadioSeekBar.setProgress(progress);
        }
    }

    public void setPlayStatus(MissAudioManager.IAudio audio) {
        if (MissAudioManager.get().isStarted(audio)) {
            mPlay.setSelected(true);
        } else {
            mPlay.setSelected(false);
        }
    }

    public void setMediaPlayProgress(int mediaPlayCurrentPosition, int totalDuration) {
        if (totalDuration != 0) {
            mTotalDuration = totalDuration;
            mRadioSeekBar.setMax(totalDuration);
            setRadioProgress(mediaPlayCurrentPosition);
            mProgressLength.setText(DateUtil.format(mediaPlayCurrentPosition / 1000 * 1000, DateUtil.FORMAT_MINUTE_SECOND));
        }
    }

    public void setRadio(Radio radio) {
        mRadio = radio;
        if (radio != null) {
            GlideApp.with(getContext())
                    .load(radio.getAudioCover())
                    .circleCrop()
                    .into(mVoiceCover);
            mRadioTotalLength.setText(DateUtil.formatMediaLength(radio.getAudioTime()));
            setListenNumber(radio.getViewNumber());
            mRadioSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
            setPlayStatus(radio);
        }
    }

    public void updateListenNumber() {
        if (mRadio != null) {
            mRadio.setViewNumber(mRadio.getViewNumber() + 1);
            setListenNumber(mRadio.getViewNumber());
        }
    }

    public void setListenNumber(int listenNumber) {
        mListenNumber.setText(String.valueOf(listenNumber));
    }

    public void onPlayStop() {
        onPlayPause();
        setRadioProgress(0);
        mProgressLength.setText(R.string.start_time);
    }

    public void onPlayPause() {
        mPlay.setSelected(false);
        mVoiceCover.clearAnimation();
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            if (mOnRadioPlayListener != null && b) {
                mOnRadioPlayListener.onSeekChange(progress);
            }
            if (progress == mTotalDuration && mRadio != null) {
                Client.submitAudioIsListenComplete(mRadio.getAudioId())
                        .fire();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @OnClick(R.id.play)
    public void onViewClicked() {
        if (mOnRadioPlayListener != null) {
            mOnRadioPlayListener.onRadioPlay();
        }
    }

}
