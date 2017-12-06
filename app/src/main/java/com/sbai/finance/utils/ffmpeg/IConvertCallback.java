package com.sbai.finance.utils.ffmpeg;

import java.io.File;

/**
 * Created by ${wangJie} on 2017/12/5.
 */

public interface IConvertCallback {

    void onSuccess(File convertedFile);

    void onFailure(Exception error);
}
