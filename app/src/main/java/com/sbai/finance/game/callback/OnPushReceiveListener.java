package com.sbai.finance.game.callback;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.sbai.socket.WsResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class OnPushReceiveListener<T> {

    public abstract void onPushReceive(T t, String originalData);

    public <W> void onOriginPushReceive(W w, String originalData) {
        try {
            T t = new Gson().fromJson(new Gson().toJson(w), getGenericType());
            onPushReceive(t, originalData);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
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
