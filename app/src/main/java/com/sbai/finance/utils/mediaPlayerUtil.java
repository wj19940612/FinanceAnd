package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class mediaPlayerUtil {

	private static MediaPlayer mMediaPlayer;
	//private static AudioManager mAudioManager;

	public static void play(String url, MediaPlayer.OnCompletionListener onCompletionListener) {

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
		/*	//1 初始化AudioManager对象
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			//2 申请焦点
			mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);*/
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mMediaPlayer.start();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isPlaying(){
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			return true;
		}
		return false;
	}

	public static void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
}