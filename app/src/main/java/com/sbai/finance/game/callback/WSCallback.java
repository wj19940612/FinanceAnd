package com.sbai.finance.game.callback;

import com.google.gson.internal.$Gson$Types;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.R;
import com.sbai.finance.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class WSCallback<T> {

    private String tag;

    public abstract void onResponse(T t);

    public void onError(int code) {
        if (!BuildConfig.IS_PROD) {
            ToastUtil.show(String.valueOf(code));
        }
    }

    public void onTimeout() {
        ToastUtil.show(R.string.http_lib_error_timeout);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public Type getGenericType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }
}
