package com.sbai.finance.view.radio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${wangJie} on 2017/11/20.
 * 姐说主页电台
 */

public class MissRadioLayout extends LinearLayout {

    private static final String TAG = "MissRadioLayout";
    private static final int DEFAULT_MISS_RADIO_LIST_SIZE = 4;
    private OnMissRadioPlayListener mOnMissRadioPlayListener;
    private ArrayList<PlayStatus> mPlayStateList;
    private ImageView mPlayImageView;
    private Radio mPlayRadio;
    private TextView mRadioTextView;

    public interface OnMissRadioPlayListener {
        void onMissRadioPlay(Radio radio);

        void onMissRadioClick(Radio radio);
    }

    public MissRadioLayout(Context context) {
        this(context, null);
    }

    public MissRadioLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissRadioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setVisibility(GONE);
        setBackgroundColor(Color.WHITE);
        mPlayStateList = new ArrayList<>();

        Rect rect = new Rect();

        getGlobalVisibleRect(rect);

    }

    public void setOnMissRadioPlayListener(OnMissRadioPlayListener onMissRadioPlayListener) {
        mOnMissRadioPlayListener = onMissRadioPlayListener;
    }

    public void setMissRadioList(List<Radio> radioList) {
        if (radioList == null) return;
        removeAllViews();
        int defaultPadding = (int) Display.dp2Px(14, getResources());
        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_miss_radio_title);
        addView(imageView, layoutParams);

        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) Display.dp2Px(9, getResources()), 0, 0);
        mPlayStateList.clear();
        if (!radioList.isEmpty()) setVisibility(VISIBLE);
        int radioListShowSize = radioList.size() > DEFAULT_MISS_RADIO_LIST_SIZE ? DEFAULT_MISS_RADIO_LIST_SIZE : radioList.size();
        for (int i = 0; i < radioListShowSize; i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_miss_radio, null);
            ImageView radioCover = view.findViewById(R.id.radioCover);
            TextView voiceName = view.findViewById(R.id.voiceName);
            TextView radioUpdateTime = view.findViewById(R.id.radioUpdateTime);
            TextView radioOwnerName = view.findViewById(R.id.radioOwnerName);
            TextView radioName = view.findViewById(R.id.radioName);
            final ImageView startPlay = view.findViewById(R.id.startPlay);
            TextView radioLength = view.findViewById(R.id.radioLength);

            Radio radio = radioList.get(i);
            GlideApp.with(getContext())
                    .load(radio.getRadioCover())
                    .into(radioCover);
            voiceName.setText(radio.getAudioName());
            radioUpdateTime.setText(DateUtil.formatDefaultStyleTime(radio.getReviewTime()));
            radioName.setText(radio.getRadioName());
            radioOwnerName.setText(radio.getRadioHostName());
            radioLength.setText(DateUtil.formatMediaLength(radio.getAudioTime()));
            startPlay.setImageResource(R.drawable.bg_voice_play);
            addView(view, layoutParams);

            PlayStatus playStatus = new PlayStatus();
            playStatus.setImageView(startPlay);
            playStatus.setRadio(radio);
            playStatus.setView(view);
            playStatus.setRadioTextView(radioLength);
            mPlayStateList.add(playStatus);
        }

        for (int i = 0; i < mPlayStateList.size(); i++) {
            final PlayStatus playStatus = mPlayStateList.get(i);
            final ImageView playImageView = playStatus.getImageView();
            View view = playStatus.getView();

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnMissRadioPlayListener != null) {
                        mOnMissRadioPlayListener.onMissRadioClick(playStatus.getRadio());
                    }
                }
            });
            playImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPlayRadio = playStatus.getRadio();
                    mRadioTextView = playStatus.getRadioTextView();
                    mPlayImageView = playImageView;
                    if (mOnMissRadioPlayListener != null) {
                        mOnMissRadioPlayListener.onMissRadioPlay(playStatus.getRadio());
                    }
                }
            });
        }
    }

    public void unChangePlay(ImageView playImageView) {
        if (mPlayStateList != null && !mPlayStateList.isEmpty()) {
            for (PlayStatus result : mPlayStateList) {
                if (result.getImageView() != playImageView) {
                    result.getImageView().setSelected(false);
                    result.getRadioTextView().setText(DateUtil.formatMediaLength(result.getRadio().getAudioTime()));
                }
            }
        }
    }

    public void updatePlayView() {
        unChangePlay(null);
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (audio instanceof Radio) {
            if (MissAudioManager.get().isPlaying()) {
                for (int i = 0; i < mPlayStateList.size(); i++) {
                    PlayStatus playStatus = mPlayStateList.get(i);
                    Radio radio = playStatus.getRadio();
                    if (radio.getId() == audio.getAudioId()) {
                        mPlayImageView = playStatus.getImageView();
                        mPlayImageView.setSelected(true);
                        mRadioTextView = playStatus.getRadioTextView();
                        mPlayRadio = radio;
                        break;
                    }
                }
            }
        }
        unChangePlay(mPlayImageView);
    }

    public void updatePlayStatus() {
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (MissAudioManager.get().isPlaying()) {
            if (audio instanceof Radio) {
                for (int i = 0; i < mPlayStateList.size(); i++) {
                    PlayStatus playStatus = mPlayStateList.get(i);
                    Radio radio = playStatus.getRadio();
                    if (radio.getId() == audio.getAudioId()) {
                        playStatus.getImageView().setSelected(true);
                        mRadioTextView = playStatus.getRadioTextView();
                        mPlayRadio = radio;
                        break;
                    }
                }
            } else if (audio instanceof Question) {
                unChangePlay(null);
            }
        }
    }

    public void setPlayRadio(int mediaPlayCurrentPosition, int totalDuration) {
        if (totalDuration == 0) return;
        if (mRadioTextView == null) {
            updatePlayStatus();
        }
        if (mRadioTextView != null) {
            if (mPlayRadio != null) {
                int scaleTime = totalDuration - mediaPlayCurrentPosition;
                mRadioTextView.setText(DateUtil.format(scaleTime, DateUtil.FORMAT_MINUTE_SECOND));
            }
        }
    }

    public void onMediaPause() {
        if (mPlayImageView != null) {
            mPlayImageView.setSelected(false);
        } else {
            unChangePlay(null);
        }
    }

    public void onMediaResume() {
        if (mPlayImageView != null) {
            mPlayImageView.setSelected(true);
        }
    }

    public void onMediaStop() {
        if (mRadioTextView != null) {
            if (mPlayRadio != null) {
                mRadioTextView.setText(DateUtil.formatMediaLength(mPlayRadio.getAudioTime()));
            }
        }
        if (mPlayImageView != null) {
            mPlayImageView.setSelected(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPlayStateList.clear();
    }

    private static class PlayStatus {
        private ImageView mImageView;
        private Radio mRadio;
        private View mView;
        private TextView mRadioTextView;

        public TextView getRadioTextView() {
            return mRadioTextView;
        }

        public void setRadioTextView(TextView radioTextView) {
            mRadioTextView = radioTextView;
        }

        public View getView() {
            return mView;
        }

        public void setView(View view) {
            mView = view;
        }

        public ImageView getImageView() {
            return mImageView;
        }

        public void setImageView(ImageView imageView) {
            mImageView = imageView;
        }

        public Radio getRadio() {
            return mRadio;
        }

        public void setRadio(Radio radio) {
            mRadio = radio;
        }
    }

}
