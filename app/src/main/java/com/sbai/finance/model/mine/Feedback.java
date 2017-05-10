package com.sbai.finance.model.mine;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class Feedback {

    public static int CONTENT_TYPE_TEXT = 1;
    public static int CONTENT_TYPE_PICTURE = 2;

    /**
     * contentType : 84125
     * content : 哈哈哈
     * createDate : 1492157398000
     * id : 232
     * status : 1
     * type : 1
     * userId : 18
     * userName : 用户55
     * userPhone : 15067061864
     */

    private int contentType;
    private String content;
    private long createDate;
    private int id;
    private int status;
    private int type;
    private int userId;
    private String userName;
    private String userPhone;

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
