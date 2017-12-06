package com.sbai.finance.utils.ffmpeg;

/**
 * Created by ${wangJie} on 2017/12/5.
 */

public interface ILoadCallback {
    void onSuccess();

    void onFailure(Exception error);
}
