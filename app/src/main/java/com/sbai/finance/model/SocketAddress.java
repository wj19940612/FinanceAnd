package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.android.volley.VolleyError;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;

import java.util.List;

public class SocketAddress implements Parcelable {

    /**
     * port : 8068
     * ip : 139.129.221.2
     */

    private String port;
    private String ip;

    public String getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.port);
        dest.writeString(this.ip);
    }

    protected SocketAddress(Parcel in) {
        this.port = in.readString();
        this.ip = in.readString();
    }

    public static final Creator<SocketAddress> CREATOR = new Creator<SocketAddress>() {
        @Override
        public SocketAddress createFromParcel(Parcel source) {
            return new SocketAddress(source);
        }

        @Override
        public SocketAddress[] newArray(int size) {
            return new SocketAddress[size];
        }
    };

    public static void requestMarketServerIpAndPort(@Nullable final Callback callback) {
        Client.getSocketAddress()
                .setCallback(new Callback2D<Resp<List<SocketAddress>>, List<SocketAddress>>() {
                    @Override
                    protected void onRespSuccessData(List<SocketAddress> data) {
                        if (data.size() > 0) {
                            if (callback != null) {
                                callback.onSuccess(data.get(0));
                            }
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                }).fireSync();
    }

    public interface Callback {
        void onSuccess(SocketAddress ipPort);

        void onFailure();
    }

    @Override
    public String toString() {
        return "ServerIpPort{" +
                "port='" + port + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
