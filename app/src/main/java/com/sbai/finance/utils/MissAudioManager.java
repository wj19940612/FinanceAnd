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

    public static MissAudioManager get() {
        if (sMissAudioManager == null) {
            sMissAudioManager = new MissAudioManager();
        }
        return sMissAudioManager;
    }

    private MyMediaPlayer mMediaPlayer;
    private volatile boolean mPreparing;
    private volatile boolean mStopPostPrepared;
    private boolean mPaused;
    private String mAudioUrl;
    private String mUuid;
    private OnCompletedListener mOnCompletedListener;

    public void setOnCompletedListener(OnCompletedListener onCompletedListener) {
        mOnCompletedListener = onCompletedListener;
    }

    public String getAudioUrl() {
        return mAudioUrl;
    }

    private final String uuid(String audioUrl, int id) {
        return id + "@" + audioUrl;
    }

    public void play(String audioUrl, int id) {
        mUuid = uuid(audioUrl, id);
        mAudioUrl = audioUrl;
        mPaused = false;
        mStopPostPrepared = false;

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
                    Log.d("MediaPlayer", "onPrepared: ");
                    mPreparing = false;

                    if (mStopPostPrepared || mPaused) {
                        stop();
                        return;
                    }

                    if (!mAudioUrl.equals(mMediaPlayer.dataSourcePath)) {
                        mMediaPlayer.reset();
                        initializeAndPrepare(mAudioUrl);
                    } else { // prepared, start
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
                    stop();
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
        Log.d("MediaPlayer", "stop: ");
        if (mMediaPlayer != null) {
            if (!mPreparing) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            } else {
                mStopPostPrepared = true;
            }
        }
    }

    public void pause() { // pause is async
        if (mMediaPlayer != null && !mPaused) {
            if (!mPreparing) {
                mMediaPlayer.pause();
            }
            mPaused = true;
        }
    }

    public void resume() { // resume is also async
        if (mMediaPlayer != null && mPaused) {
            if (!mPreparing) {
                mMediaPlayer.start();
            }
            mPaused = false;
        }
    }

    public boolean isPaused(String audioUrl, int id) {
        if (mMediaPlayer != null) {
            return uuid(audioUrl, id).equals(mUuid) && mPaused;
        }
        return false;
    }

    public boolean isPlaying(String audioUrl, int id) {
        if (mMediaPlayer != null) {
            return uuid(audioUrl, id).equals(mUuid) && !mPaused;
        }
        return false;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
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
