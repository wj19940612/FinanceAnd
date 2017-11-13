package com.sbai.finance.game;

import com.sbai.finance.game.callback.WSCallback;

public abstract class WSRequest<T> {

    private WSCallback<T> callback;
    private boolean isRetry;

    public WSRequest(WSCallback<T> callback, boolean isRetry) {
        this.callback = callback;
        this.isRetry = isRetry;
    }

    public WSRequest(boolean isRetry) {
        this.isRetry = isRetry;
    }

    public WSCallback<T> getCallback() {
        return callback;
    }

    public void setCallback(WSCallback<T> callback) {
        this.callback = callback;
    }

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
    }
}
