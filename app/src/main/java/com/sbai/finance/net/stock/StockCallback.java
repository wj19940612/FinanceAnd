package com.sbai.finance.net.stock;

import android.util.Log;

import com.android.volley.NetworkError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.$Gson$Types;
import com.sbai.finance.net.Callback;
import com.sbai.httplib.NullResponseError;
import com.sbai.httplib.RequestManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class StockCallback<T, D> extends Callback<T> {

    public StockCallback() {
        super();
    }

    @Override
    public void onSuccess(T t) {
        Log.d(RequestManager.TAG, getUrl() + " onSuccess: " + t.toString());

        if (t instanceof StockResp) {
            List<JsonObject> resultList = ((StockResp) t).getResult();
            if (!resultList.isEmpty()) {
                StockResp.Msg msg = getMsg(resultList);
                if (msg.getError_no().equals("3000")) {
                    onDataMsg(getData(resultList), msg);
                } else {
                    onFailure(new NetworkError());
                }
            } else {
                onFailure(new NullResponseError("Server return result[]"));
            }
        }
    }

    private StockResp.Msg getMsg(List<JsonObject> resultList) {
        for (JsonObject jsonObject : resultList) {
            if (jsonObject.has("msg")) {
                JsonObject msg = jsonObject.getAsJsonObject("msg");
                return new Gson().fromJson(msg, StockResp.Msg.class);
            }
        }
        return new StockResp.Msg();
    }

    private D getData(List<JsonObject> resultList) {
        for (JsonObject jsonObject : resultList) {
            if (jsonObject.has("data")) {
                JsonElement msg = jsonObject.get("data");
                return new Gson().fromJson(msg, getDataType());
            }
        }
        return null;
    }

    public abstract void onDataMsg(D result, StockResp.Msg msg);

    @Override
    protected void onRespSuccess(T resp) {

    }

    public Type getDataType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[1]);
    }
}
