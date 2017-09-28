package com.sbai.finance.utils;

import android.content.Context;
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

public class MediaPlayerManager {

	private static MediaPlayerManager mInstance;
	private  static MediaPlayer mMediaPlayer;
	private  AudioManager mAudioManager;
	private boolean mIsPause;
	private Context mContext;
	private CountDownTimer mCountDownTimer;
	private int mSoundTime;

	private MediaPlayerManager(Context context) {
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mContext = context;
	}

	public static MediaPlayerManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (MediaPlayerManager.class) {
				if (mInstance == null) {
					mInstance = new MediaPlayerManager(context);
				}
			}
		}
		return mInstance;
	}


	public void play(String url, MediaPlayer.OnCompletionListener onCompletionListener) {
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
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
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
