package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class mediaPlayerUtil {

	private static MediaPlayer mMediaPlayer;

	public static void play(String url) {

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
			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mMediaPlayer != null) {
						mMediaPlayer.release();
						mMediaPlayer = null;
					}
				}
			});
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

	public static void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
}
