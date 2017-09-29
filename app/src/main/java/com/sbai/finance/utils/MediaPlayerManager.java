package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sbai.finance.R;

import java.io.IOException;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class MediaPlayerManager implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

	private OnCompletionListener mOnCompletionListener;

	interface OnCompletionListener {
		void onCompletion();
	}

	public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
		this.mOnCompletionListener = onCompletionListener;
	}

	private static MediaPlayerManager mInstance;
	private  MediaPlayer mMediaPlayer;
	private  AudioManager mAudioManager;
	private boolean mIsPause;
	private CountDownTimer mCountDownTimer;
	private int mSoundTime;

	public static MediaPlayerManager getInstance() {
		if (mInstance == null) {
			synchronized (MediaPlayerManager.class) {
				if (mInstance == null) {
					mInstance = new MediaPlayerManager();
				}
			}
		}
		return mInstance;
	}

	public void initAudioManager(AudioManager audioManager) {
		this.mAudioManager = audioManager;
	}

	private MediaPlayerManager() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnCompletionListener(this);
	}

	public void play(String url) {
		try {
			if ( mMediaPlayer!= null) {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(url);
				mMediaPlayer.prepareAsync();
				mMediaPlayer.setOnPreparedListener(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
		}
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mOnCompletionListener != null) {
			mOnCompletionListener.onCompletion();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		int result = mAudioManager.requestAudioFocus(afChangeListener,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			mMediaPlayer.start();
		}
	}

	public void play(String url, final ProgressBar progressBar, final TextView voiceTime,
	                 MediaPlayer.OnCompletionListener onCompletionListener) {
		try {
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						mMediaPlayer.reset();
						return false;
					}
				});
			} else {
				mMediaPlayer.reset();
			}

			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					int result = mAudioManager.requestAudioFocus(afChangeListener,
							AudioManager.STREAM_MUSIC,
							AudioManager.AUDIOFOCUS_GAIN);
					if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
						mMediaPlayer.start();
						setProgressBar(progressBar);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setProgressBar(final ProgressBar progressBar) {
		progressBar.setMax(mMediaPlayer.getDuration());
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					progressBar.setProgress(mMediaPlayer.getCurrentPosition());
					try {
						// 每100毫秒更新一次位置
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}


	private void setCountDownTime(final int soundTime, final TextView voiceTime) {
		mCountDownTimer = new CountDownTimer(soundTime * 1000 + 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				voiceTime.setText(mContext.getString(R.string.voice_time, millisUntilFinished / 1000 - 1));
				mSoundTime = (int) millisUntilFinished / 1000;
			}

			@Override
			public void onFinish() {

			}
		}.start();
	}

	public void pause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			mIsPause = true;
		}
	}

	public void resume() {
		if (mMediaPlayer != null && mIsPause) {
			mMediaPlayer.start();
			mIsPause = false;
		}
	}

	public void release() {
		if (mMediaPlayer != null) {
			mAudioManager.abandonAudioFocus(afChangeListener);
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public static int getCurrentDuration() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public static int getDuration() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getDuration();
		}
		return 0;
	}

	AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				if (!mMediaPlayer.isPlaying()) {
					mMediaPlayer.start();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				mAudioManager.abandonAudioFocus(afChangeListener);

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
			}
		}
	};
}
