package com.sbai.httplib;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @param <T>
 */
public abstract class ApiCallback<T> implements Response.Listener<T>, Response.ErrorListener {

    public interface onFinishedListener {
        void onFinished(String tag, String url);
    }

    private String mUrl;
    private onFinishedListener mOnFinishedListener;
    private String mTag;
    private ApiIndeterminate mIndeterminate;

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setOnFinishedListener(onFinishedListener onFinishedListener) {
        mOnFinishedListener = onFinishedListener;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setIndeterminate(ApiIndeterminate Indeterminate) {
        mIndeterminate = Indeterminate;
    }

    public void onStart() {
        if (mIndeterminate != null) {
            mIndeterminate.onHttpUiShow(mTag);
        }
    }

    public void onFinish() {
        if (mOnFinishedListener != null) {
            mOnFinishedListener.onFinished(mTag, mUrl);
        }

        if (mIndeterminate != null) {
            mIndeterminate.onHttpUiDismiss(mTag);
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(VolleyError volleyError);

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        onFinish();
        onFailure(volleyError);
    }

    @Override
    public void onResponse(T t) {
        Log.d(RequestManager.TAG, getUrl() + " onResponse: " + t.toString());

        onFinish();
        if (t != null) {
            onSuccess(t);
        } else {
            onFailure(new NullResponseError("Server return null"));
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
