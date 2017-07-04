package com.sbai.finance.websocket.callback;

import com.google.gson.internal.$Gson$Types;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class WSCallback<T> {

    public abstract void onResponse(T t);
    public void onError(int code){
        if (!BuildConfig.IS_PROD) {
            ToastUtil.show(String.valueOf(code));
        }
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
