package com.sbai.finance.net;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sbai.finance.App;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.httplib.ApiCallback;
import com.sbai.httplib.NullResponseError;
import com.sbai.httplib.RequestManager;

public abstract class Callback<T> extends ApiCallback<T> {

    private boolean mErrorToast;

    public Callback() {
        mErrorToast = true;
    }

    public Callback(boolean errorToast) {
        mErrorToast = errorToast;
    }

    @Override
    public void onSuccess(T t) {
        Log.d(RequestManager.TAG, getUrl() + " onSuccess: " + t.toString());

        if (t instanceof Resp) {
            Resp resp = (Resp) t;
            if (resp.isTokenExpired()) {
                processTokenExpiredError(resp);
            } else {
                onReceive(t);
            }
        } else if (t instanceof String) {
            if (((String) t).indexOf("code") != -1) {
                try {
                    Resp resp = new Gson().fromJson((String) t, Resp.class);
                    if (resp.isTokenExpired()) {
                        processTokenExpiredError(resp);
                    }
                } catch (JsonSyntaxException e) {
                    onReceive(t);
                }
            } else {
                onReceive(t);
            }
        } else {
            onReceive(t);
        }
    }

    private void processTokenExpiredError(Resp resp) {
        sendTokenExpiredBroadcast(resp.getMsg());
        onFailure(null);
    }

    private void sendTokenExpiredBroadcast(String msg) {
        Intent intent = new Intent(BaseActivity.ACTION_TOKEN_EXPIRED);
        intent.putExtra(BaseActivity.EX_TOKEN_EXPIRED_MESSAGE, msg);
        LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(intent);
    }

    @Override
    public void onFailure(VolleyError volleyError) {
        if (volleyError == null) return;

        Log.d(RequestManager.TAG, getUrl() + " " + volleyError.toString());

        int toastResId = R.string.http_lib_error_network;
        if (volleyError instanceof NullResponseError) {
            toastResId = R.string.http_lib_error_null;
        } else if (volleyError instanceof TimeoutError) {
            toastResId = R.string.http_lib_error_timeout;
        } else if (volleyError instanceof ParseError) {
            toastResId = R.string.http_lib_error_parse;
        } else if (volleyError instanceof NetworkError) {
            toastResId = R.string.http_lib_error_network;
        } else if (volleyError instanceof ServerError) {
            toastResId = R.string.http_lib_error_server;
        }

        if (mErrorToast) {
            ToastUtil.show(toastResId);
        }
    }

    protected void onReceive(T t) {
        if (t instanceof Resp) {
            Resp resp = (Resp) t;
            if (resp.isSuccess()) {
                onRespSuccess(t);
            } else {
                onFailure(null);
                onToastErrorMessage(resp.getMsg());
            }
        } else {
            onRespSuccess(t);
        }
    }

    protected abstract void onRespSuccess(T resp);

    protected void onToastErrorMessage(String msg) {
        if (mErrorToast) {
            ToastUtil.show(msg);
        }
    }
}
