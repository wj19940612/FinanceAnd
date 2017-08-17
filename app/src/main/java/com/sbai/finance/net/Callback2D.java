package com.sbai.finance.net;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.sbai.httplib.BuildConfig;
import com.sbai.httplib.NullResponseError;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 只在成功的时候（Resp's code = 200）时回调解析出 Resp's Data，并且判断 Data 是否为空，空处理类似 {@link NullResponseError} 的处理
 *
 * @param <T> Type of Resp
 * @param <D> Type of Data
 */
public abstract class Callback2D<T, D> extends Callback<T> {

    public Callback2D() {
        super();
    }

    @Override
    protected final void onRespSuccess(T t) {
        if (t instanceof Resp) {
            Resp resp = (Resp) t;
            Object data = resp.getData();
            if (data != null) {
                if (data instanceof String) {
                    data = onInterceptData((String) data);
                    Object o = new Gson().fromJson((String) data, getDataType());
                    onRespSuccessData((D) o);
                } else {
                    onRespSuccessData((D) data);
                }
            } else {
                onFailure(null);
                if (BuildConfig.DEBUG) {
                    onRespFailure(resp);
                }
            }
        }
    }


    /**
     * Called only when T.data is String type
     *
     * @param data
     * @return
     */
    protected String onInterceptData(String data) {
        return data;
    }

    protected abstract void onRespSuccessData(D data);

    public Type getDataType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[1]);
    }
}
