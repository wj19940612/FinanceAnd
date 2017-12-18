package com.sbai.finance.utils.audio;

import android.media.AudioRecord;
import android.util.Log;

import com.sbai.finance.App;
import com.sbai.finance.R;
import com.sbai.finance.utils.FileUtils;
import com.songbai.mp3record.MP3Recorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by ${wangJie} on 2017/11/30.
 * 录制音频
 */

public class MediaRecorderManager {
    private static final String TAG = "MediaRecorderManager";

    public static final int RECORD_MEDIA_ERROR_SYSTEM_CODE = 0;  //录制音频系统抛出的错误
    public static final int RECORD_MEDIA_ERROR_CODE_PERMISSION = 1;  //自己定义的没有权限错误code

    private static final int DEFAULT_MAX_AUDIO_LENGTH = 10 * 60 * 1000;  //最大时间长度

    private volatile boolean mPreparing;
    private String mRecordAudioPath;
    private MediaMediaRecorderPrepareListener mMediaMediaRecorderPrepareListener;
    private MP3Recorder mMP3Recorder;

    public interface MediaMediaRecorderPrepareListener {
        void onMediaMediaRecorderPrepared(String path);

        void onError(int what, Exception e);
    }

    public void setMediaMediaRecorderPrepareListener(MediaMediaRecorderPrepareListener mediaMediaRecorderPrepareListener) {
        mMediaMediaRecorderPrepareListener = mediaMediaRecorderPrepareListener;
    }

    public MediaRecorderManager() {
    }

    private String getOutputFilePath(String path) {
        File file = FileUtils.createFile(App.getAppContext().getString(R.string.app_name) + System.currentTimeMillis() + path);
        mRecordAudioPath = file.getAbsolutePath();
        return file.getAbsolutePath();
    }

    public String getRecordAudioPath() {
        return mRecordAudioPath;
    }

    public void onRecordStart() {
        String outputFilePath = getOutputFilePath(".mp3");
        if (mMP3Recorder != null) {
            mMP3Recorder.stop();
            mMP3Recorder = null;
        }
        mMP3Recorder = new MP3Recorder(new File(outputFilePath));
        try {
            mMP3Recorder.start();
            if (mMediaMediaRecorderPrepareListener != null) {
                mMediaMediaRecorderPrepareListener.onMediaMediaRecorderPrepared(outputFilePath);
            }

            int recordingState = mMP3Recorder.getRecordingState();
            if (recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                if (mMediaMediaRecorderPrepareListener != null) {
                    mMediaMediaRecorderPrepareListener.onError(RECORD_MEDIA_ERROR_CODE_PERMISSION, new Exception("   State is not Recording "));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mMediaMediaRecorderPrepareListener != null) {
                mMediaMediaRecorderPrepareListener.onError(RECORD_MEDIA_ERROR_SYSTEM_CODE, e);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            if (mMediaMediaRecorderPrepareListener != null) {
                mMediaMediaRecorderPrepareListener.onError(RECORD_MEDIA_ERROR_SYSTEM_CODE, e);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            if (mMediaMediaRecorderPrepareListener != null) {
                mMediaMediaRecorderPrepareListener.onError(RECORD_MEDIA_ERROR_SYSTEM_CODE, e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onRecordStart: " + e.toString());
        }
    }


    public void onRecordStop() {
        if (mMP3Recorder != null) {
            mMP3Recorder.stop();
        }
    }

    public void onDestroy() {
        deleteRadioFile();
        onRecordStop();
        mMP3Recorder = null;
    }

    public void deleteRadioFile() {
        if (mRecordAudioPath != null) {
            File file = new File(mRecordAudioPath);
            if (file != null && file.exists()) {
                file.delete();
                mRecordAudioPath = null;
            }
        }
    }
}
