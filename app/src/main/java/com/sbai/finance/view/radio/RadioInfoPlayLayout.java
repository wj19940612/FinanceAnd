package com.sbai.finance.view.radio;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.MissAudioManager;
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


    public interface OnRadioPlayListener {
        void onRadioPlay();

        void onSeekChange(int progress);
    }

    public void mOnRadioPlayListener(OnRadioPlayListener onRadioPlayListener) {
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
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBind.unbind();
        mOnSeekBarChangeListener = null;
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

    public void setRadio(Radio radio) {
        mRadio = radio;
        GlideApp.with(getContext())
                .load(radio.getAudioCover())
                .circleCrop()
                .into(mVoiceCover);
        mRadioTotalLength.setText(DateUtil.format(radio.getAudioTime(), DateUtil.FORMAT_HOUR_MINUTE));
        mListenNumber.setText(String.valueOf(radio.getViewNumber()));
        mRadioSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        setPlayStatus(radio);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            if (mOnRadioPlayListener != null) {
                mOnRadioPlayListener.onSeekChange(progress);
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
