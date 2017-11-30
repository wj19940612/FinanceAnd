package com.sbai.finance.utils.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.sbai.finance.App;

import java.io.File;
import java.io.IOException;

import static android.media.MediaRecorder.MEDIA_ERROR_SERVER_DIED;
import static android.media.MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN;
import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;
import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED;
import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN;

/**
 * Created by ${wangJie} on 2017/11/30.
 * 录制音频
 */

public class MediaRecorderManager implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {
    private static final String TAG = "MediaRecorderManager";

    private static final int DEFAULT_MAX_AUDIO_LENGTH = 10 * 60 * 1000;  //最大时间长度

    private MediaRecorder mMediaRecorder;
    private volatile boolean mPreparing;
    private String mRecordAudioPath;
    private MediaMediaRecorderPrepareListener mMediaMediaRecorderPrepareListener;

    public interface MediaMediaRecorderPrepareListener {
        void onMediaMediaRecorderPrepared();
    }

    public void setMediaMediaRecorderPrepareListener(MediaMediaRecorderPrepareListener mediaMediaRecorderPrepareListener) {
        mMediaMediaRecorderPrepareListener = mediaMediaRecorderPrepareListener;
    }

    public MediaRecorderManager() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnInfoListener(this);

        mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //输出格式为 aac 格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        //所有安卓系统都支持采样频率
        mMediaRecorder.setAudioSamplingRate(44100);
        //通用的 AAC 编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //音质比较好的频率
        mMediaRecorder.setAudioEncodingBitRate(96000);
        //设置最大语音时间长度
        mMediaRecorder.setMaxFileSize(DEFAULT_MAX_AUDIO_LENGTH);
        //设置录音文件配置
        mMediaRecorder.setOutputFile(getOutputFilePath());
    }

    private String getOutputFilePath() {
        String absolutePath;
        Context appContext = App.getAppContext();
        if (appContext.getExternalCacheDir() != null) {
            absolutePath = appContext.getExternalCacheDir().getAbsolutePath();
        } else {
        absolutePath = Environment.getExternalStorageDirectory() + "/miss_voice";
        }
        mRecordAudioPath = System.currentTimeMillis() + ".aac";
        File dir = new File(absolutePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, mRecordAudioPath);
        Log.d(TAG, "getOutputFilePath: " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public void onRecordStart() {
        //配置 MediaRecorder
        mPreparing = false;
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mPreparing = true;
            if (mMediaMediaRecorderPrepareListener != null) {
                mMediaMediaRecorderPrepareListener.onMediaMediaRecorderPrepared();
            }
        } catch (IOException e) {
            mPreparing = false;
            Log.d(TAG, "onPrepare: " + e.toString());
            e.printStackTrace();
        }
    }

    private void onRecordRelease() {
        try {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            mMediaRecorder.release();
            mMediaRecorder.reset();
            mMediaRecorder.stop();
        } catch (Exception e) {
            Log.d(TAG, "onRecordRelease: " + e.toString());
            e.printStackTrace();
        }
    }

    public void onRecordReset() {
        mMediaRecorder.reset();
    }

    public void onRecordStop() {
        deleteRadioFile();
        if (mMediaRecorder != null) {
            onRecordRelease();
            mMediaRecorder = null;
        }
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

    @Override
    public void onError(MediaRecorder mediaRecorder, int what, int extra) {
        switch (what) {
            case MEDIA_RECORDER_ERROR_UNKNOWN:
                Log.d(TAG, "onError: " + MEDIA_RECORDER_ERROR_UNKNOWN);
                break;
            case MEDIA_ERROR_SERVER_DIED:   //  Media server died. In this case, the application must release the  * MediaRecorder object and instantiate a new one.
                Log.d(TAG, "onError: " + MEDIA_ERROR_SERVER_DIED);
                break;
        }
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        switch (what) {
            case MEDIA_RECORDER_INFO_UNKNOWN:
                Log.d(TAG, "onInfo: " + MEDIA_RECORDER_INFO_UNKNOWN);
                break;
            case MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:   //到达最大时间长度
                Log.d(TAG, "onInfo: " + MEDIA_RECORDER_INFO_MAX_DURATION_REACHED);
                break;
            case MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:  //到答案最大文件大小
                Log.d(TAG, "onInfo: " + MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED);
                break;
        }
    }
}
