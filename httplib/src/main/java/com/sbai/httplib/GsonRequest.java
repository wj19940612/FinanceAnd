package com.sbai.httplib;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {

    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final Type type;
    private final Listener<T> listener;
    private final String body;

    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    public GsonRequest(int method, String url, ApiHeaders headers, ApiParams params, String body,
                       Type type, ApiCallback<T> callback) {
        super(method, url, callback);

        this.headers = headers != null ? headers.get() : null;
        this.params = params != null ? params.get() : null;
        this.type = type;
        this.listener = callback;
        this.body = body;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (params != null && !params.isEmpty()) {
            return params;
        }
        return super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return body != null ? PROTOCOL_CONTENT_TYPE : super.getBodyContentType();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] defaultBytes = super.getBody();
        try {
            return body != null ? body.toString().getBytes(PROTOCOL_CHARSET) : defaultBytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return defaultBytes;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String json;
        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            CookieManger.getInstance().parse(response.headers);

            T result;
            if (type instanceof Class && ((Class) type).getName().equals(String.class.getName())) {
                result = (T) new String(json);
            } else {
                result = new Gson().fromJson(json, type);
            }

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (getMethod()) {
            case Request.Method.GET:
                builder.append("GET");
                break;
            case Request.Method.PUT:
                builder.append("PUT");
                break;
            case Request.Method.POST:
                builder.append("POST");
                break;
            case Request.Method.DELETE:
                builder.append("DELETE");
                break;
        }

        builder.append(" ").append(getUrl());
        buildBody(builder);
        buildParams(builder);
        buildHeader(builder);

        return builder.toString();
    }

    private void buildBody(StringBuilder builder) {
        if (body != null) {
            builder.append(body);
        }
    }

    private void buildParams(StringBuilder builder) {
        if (params != null && !params.isEmpty()) {
            builder.append("?");
            for (Object key : params.keySet()) {
                builder.append(key).append("=")
                        .append(params.get(key)).append("&");
            }
            if (builder.toString().endsWith("&")) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
    }

    private void buildHeader(StringBuilder builder) {
        if (headers != null) {
            for (Object key : headers.keySet()) {
                builder.append(" -H ").append('\'')
                        .append(key).append(": ")
                        .append(headers.get(key)).append('\'');
            }
        }
    }
}
