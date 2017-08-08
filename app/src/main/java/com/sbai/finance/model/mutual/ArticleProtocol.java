package com.sbai.finance.model.mutual;

/**
 * 2 借款协议  3 用户协议  4 兑换规则  6充值服务协议
 */

public class ArticleProtocol {

    public static final int PROTOCOL_BORROW = 2;
    public static final int PROTOCOL_USER = 3;
    public static final int PROTOCOL_EXCHANGE = 4;
    public static final int PROTOCOL_BATTLE = 5;
    public static final int PROTOCOL_RECHARGE_SERVICE = 6;

    /**
     * content : 如影随形
     * createTime : 1494815994268
     * id : 10
     * modifyTime : 1494816290843
     * title : 改个标题试试
     */

    private String content;
    private long createTime;
    private int id;
    private long modifyTime;
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
