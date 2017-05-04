package com.sbai.finance.model.mine;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/4/19.
 * 被屏蔽的用户model
 */

public class ShieldedUserModel {
    /**
     * data : [{"createTime":1493827200000,"id":88,"shielduserId":105,"shielduserName":"用户317","shielduserPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493863530692.png"}]
     * pageSize : 15
     * resultCount : 1
     * start : 0
     * total : 1
     */

    private int pageSize;
    private int resultCount;
    private int start;
    private int total;
    private List<DataBean> data;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * createTime : 1493827200000
         * id : 88
         * shielduserId : 105
         * shielduserName : 用户317
         * shielduserPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493863530692.png
         */

        private long createTime;
        private int id;
        private int shielduserId;
        private String shielduserName;
        private String shielduserPortrait;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getShielduserId() {
            return shielduserId;
        }

        public void setShielduserId(int shielduserId) {
            this.shielduserId = shielduserId;
        }

        public String getShielduserName() {
            return shielduserName;
        }

        public void setShielduserName(String shielduserName) {
            this.shielduserName = shielduserName;
        }

        public String getShielduserPortrait() {
            return shielduserPortrait;
        }

        public void setShielduserPortrait(String shielduserPortrait) {
            this.shielduserPortrait = shielduserPortrait;
        }
    }

    /**
     * createTime : 74861
     * id : 88525
     * shielduserId : 66584
     * shielduserName : 测试内容4749
     * shielduserPortrait : 测试内容a320
     */
}
