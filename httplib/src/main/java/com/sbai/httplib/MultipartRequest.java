package com.sbai.httplib;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件上传
 */

public class MultipartRequest<T> extends Request<T> {
	private final Map<String, String> headers;
	private MultipartEntity entity = new MultipartEntity();
	private Response.Listener<T> mListener;
	private final Type type;
	private List<File> mFileParts;
	private String mFilePartName;
	private final Map<String, String> params;

	/**
	 * 单个文件＋参数 上传
	 *
	 * @param method
	 * @param url
	 * @param filePartName
	 * @param file
	 * @param params
	 */
	public MultipartRequest(int method, String url, ApiHeaders headers, String filePartName, File file, ApiParams params,
                            Type type, ApiCallback<T> callback) {
		super(Method.POST, url, callback);
		mFileParts = new ArrayList<File>();
		if (file != null && file.exists()) {
			mFileParts.add(file);
		} else {
			VolleyLog.e("MultipartRequest---file not found");
		}
		this.headers = headers != null ? headers.get() : null;
		this.params = params != null ? params.get() : null;
		mFilePartName = filePartName;
		this.type = type;
		mListener = callback;
		buildMultipartEntity();
	}

	/**
	 * 多个文件＋参数上传
	 *
	 * @param url
	 * @param filePartName
	 * @param files
	 * @param params
	 */
	public MultipartRequest(int method, String url, ApiHeaders headers, String filePartName, List<File> files, ApiParams params, Type type, ApiCallback<T> callback) {
		super(Method.POST, url, callback);
		this.headers = headers != null ? headers.get() : null;
		this.params = params != null ? params.get() : null;
		mFilePartName = filePartName;
		this.type = type;
		mListener = callback;
		mFileParts = files;
		buildMultipartEntity();
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
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	private void buildMultipartEntity() {
		if (mFileParts != null && mFileParts.size() > 0) {
			for (File file : mFileParts) {
				entity.addFilePart(mFilePartName, file);
			}
			long l = entity.getContentLength();
			Log.i("YanZi-volley", mFileParts.size() + "个，长度：" + l);
		}

		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				entity.addStringPart(entry.getKey(), entry.getValue());
			}
		}
	}
}
