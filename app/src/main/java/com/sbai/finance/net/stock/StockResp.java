package com.sbai.finance.net.stock;

import com.google.gson.JsonObject;

import java.util.List;

public class StockResp {

    private List<JsonObject> result;

    public List<JsonObject> getResult() {
        return result;
    }

    public static class Msg {
        /**
         * error_info : OK
         * error_no : 3000
         */
        private String error_info;
        private String error_no;

        public String getError_info() {
            return error_info;
        }

        public String getError_no() {
            return error_no;
        }

        @Override
        public String toString() {
            return "MsgBean{" +
                    "error_info='" + error_info + '\'' +
                    ", error_no='" + error_no + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "StockResp{" +
                "result=" + result +
                '}';
    }
}
