package com.sbai.finance.net;

import com.sbai.httplib.NullResponseError;

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

    public Callback2D(boolean errorVisible) {
        super(errorVisible);
    }

    @Override
    protected void onRespSuccess(T t) {
        if (t instanceof Resp) {
            Resp resp = (Resp) t;
            D data = (D) resp.getData();
            if (data != null) {
                onRespSuccessData(data);
            } else {
                onFailure(new NullResponseError(NullResponseError.RESP_DATA_NULL,
                        "Response's data is null"));
            }
        }
    }

    protected abstract void onRespSuccessData(D data);
}
