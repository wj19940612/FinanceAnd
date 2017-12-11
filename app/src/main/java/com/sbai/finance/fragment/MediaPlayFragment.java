package com.sbai.finance.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Client;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.MissFloatWindow;

/**
 * Created by ${wangJie} on 2017/11/23.
 */

public abstract class MediaPlayFragment extends BaseFragment {


    public MediaPlayService mMediaPlayService;
    protected MissFloatWindow mRootMissFloatWindow;

    @Override
    public void onResume() {
        super.onResume();
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (mRootMissFloatWindow != null) {
            if (audio instanceof Question) {
                Question playQuestion = (Question) audio;
                mRootMissFloatWindow.setMissAvatar(playQuestion.getCustomPortrait());
            } else if (audio instanceof Radio) {
                mRootMissFloatWindow.setMissAvatar(((Radio) audio).getUserPortrait());
            }
            if (!MissAudioManager.get().isPlaying()) {
                mRootMissFloatWindow.setVisibility(View.GONE);
            }
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMediaPlayBroadcastReceiver, getIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMediaPlayBroadcastReceiver);
    }


    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_START);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_RESUME);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PAUSE);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_STOP);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_ERROR);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PROGRESS);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PLAY);
        return filter;
    }

    protected void updateRadioListen(Radio radio) {
        if (radio != null)
            Client.listenRadioAudio(radio.getAudioId())
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .fireFree();
    }

    public MediaPlayService getMediaPlayService() {
        return mMediaPlayService;
    }

    public abstract void onMediaPlayStart(int IAudioId, int source);

    public abstract void onMediaPlay(int IAudioId, int source);

    public abstract void onMediaPlayResume(int IAudioId, int source);

    public abstract void onMediaPlayPause(int IAudioId, int source);

    protected abstract void onMediaPlayStop(int IAudioId, int source);

    protected void onMediaPlayError(int IAudioId, int source) {
        ToastUtil.show(R.string.play_failure);
    }

    public void onOtherReceive(Context context, Intent intent) {

    }

    protected void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {

    }


    private MediaPlayService.MediaPlayBroadcastReceiver mMediaPlayBroadcastReceiver = new MediaPlayService.MediaPlayBroadcastReceiver() {
        @Override
        public void onOtherReceive(Context context, Intent intent) {
            MediaPlayFragment.this.onOtherReceive(context, intent);
        }

        @Override
        public void onMediaPlayStart(int IAudioId, int source) {
            MediaPlayFragment.this.onMediaPlayStart(IAudioId, source);
        }

        @Override
        public void onMediaPlayResume(int IAudioId, int source) {
            MediaPlayFragment.this.onMediaPlayResume(IAudioId, source);
        }

        @Override
        public void onMediaPlayPause(int IAudioId, int source) {
            MediaPlayFragment.this.onMediaPlayPause(IAudioId, source);
        }

        @Override
        public void onMediaPlayStop(int IAudioId, int source) {
            MediaPlayFragment.this.onMediaPlayStop(IAudioId, source);
        }

        @Override
        public void onMediaPlayError(int IAudioId, int source) {
            MediaPlayFragment.this.onMediaPlayError(IAudioId, source);
        }

        @Override
        public void onMediaPlay(int IAudioId, int source) {
            MediaPlayFragment.this.onMediaPlay(IAudioId, source);
        }

        @Override
        public void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {
            MediaPlayFragment.this.onMediaPlayCurrentPosition(IAudioId, source, mediaPlayCurrentPosition, totalDuration);
        }
    };

}
