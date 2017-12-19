package com.songbai.mp3record;

/**
 * Created by ${wangJie} on 2017/12/19.
 */

public interface OnMp3RecordListener {

    public void onMp3RecordStart();

    public void onMp3RecordError(Exception e);
}
