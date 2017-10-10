package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MissAudioManager {

    public interface OnCompletedListener {
        void onCompleted(String url);
    }

    private static MissAudioManager sMissAudioManager;

    public MissAudioManager get() {
        if (sMissAudioManager == null) {
            sMissAudioManager = new MissAudioManager();
        }
        return sMissAudioManager;
    }

    private MyMediaPlayer mMediaPlayer;
    private boolean mPreparing;
    private String mAudioUrl;
    private OnCompletedListener mOnCompletedListener;

    public void setOnCompletedListener(OnCompletedListener onCompletedListener) {
        mOnCompletedListener = onCompletedListener;
    }

    public void play(String audioUrl) {
        mAudioUrl = audioUrl;

        if (mPreparing) return;

        stop();

        if (mMediaPlayer == null) {
            mMediaPlayer = new MyMediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d("MediaPlayer", "onError: " + what + ", extra: " + extra);
                    mp.reset();
                    return false;
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPreparing = false;
                    if (!mAudioUrl.equals(mMediaPlayer.dataSourcePath)) {
                        mMediaPlayer.reset();
                        initializeAndPrepare(mAudioUrl);
                    } else { // prepared, keep go
                        mMediaPlayer.start();
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mOnCompletedListener != null) {
                        mOnCompletedListener.onCompleted(mAudioUrl);
                    }

                }
            });
        }

        mMediaPlayer.reset();
        initializeAndPrepare(audioUrl);
    }

    private void initializeAndPrepare(String audioUrl) {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(audioUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mPreparing = true;
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void pause() { // pause is async
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public boolean isPaused() {
        if (mMediaPlayer != null) {
            return !mMediaPlayer.isPlaying();
        }
        return true;
    }

    private static class MyMediaPlayer extends MediaPlayer {

        private String dataSourcePath;

        @Override
        public void setDataSource(String path)
                throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
            super.setDataSource(path);
            dataSourcePath = path;
        }
    }
}
