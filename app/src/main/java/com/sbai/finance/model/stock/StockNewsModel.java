package com.sbai.finance.model.stock;

/**
 * Created by ${wangJie} on 2017/5/15.
 */

public class StockNewsModel {

    /**
     * content :
     * createDate : 1494387823591
     * from : sina
     * id : 59128ccc55ce59dd30482b78
     * stockCode : 000001
     * time : 1494380400000
     * title : 32股“破净”银行股占四成 26家公司净利同比增幅为正
     * url : http://finance.sina.com.cn/stock/hyyj/2017-05-10/doc-ifyeychk7207559.shtml
     */

    private String content;
    private long createDate;
    private String from;
    private String id;
    private String stockCode;
    private long time;
    private String title;
    private String url;


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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "StockNewsModel{" +
                "content='" + content + '\'' +
                ", createDate=" + createDate +
                ", from='" + from + '\'' +
                ", id='" + id + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
