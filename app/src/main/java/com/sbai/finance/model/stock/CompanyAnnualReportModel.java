package com.sbai.finance.model.stock;

/**
 * Created by ${wangJie} on 2017/5/15.
 * 公司年度报表
 */

public class CompanyAnnualReportModel {
    //type 	stock_news：个股资讯，company_announcement：公司公告；annual_report：年度报告；financial_summary：财务摘要
    public static final String TYPE_STOCK_NEWS = "stock_news";
    public static final String TYPE_COMPANY_ANNOUNCEMENT = "company_announcement";
    public static final String TYPE_ANNUAL_REPORT = "annual_report";
    public static final String TYPE_FINANCIAL_SUMMARY = "financial_summary";


    /**
     * caiwufeiyong : 95,844,600.00元
     * changqifuzaiheji : 2,469,630,000.00元
     * createDate : 1493796956291
     * from : sina
     * gudingzichanheji :  
     * id : 5909885cd4c625c5d8245495
     * jiezhiriqi : 2016-06-30
     * jinlirun : 68,423,500.00元
     * liudongzichanheji : 7,413,390,000.00元
     * meigujinzichan : 6.0048元
     * meigushouyi : 0.0633元
     * meiguxianjinhanliang : 0.3817元
     * meiguzibengongjijin : 3.1115元
     * stockCode : 600549
     * zhuyingyewushouru : 3,499,390,000.00元
     * zichanzongji : 15,667,500,000.00元
     */

    private String caiwufeiyong;
    private String changqifuzaiheji;
    private long createDate;
    private String from;
    private String gudingzichanheji;
    private String id;
    private String jiezhiriqi;
    private String jinlirun;
    private String liudongzichanheji;
    private String meigujinzichan;
    private String meigushouyi;
    private String meiguxianjinhanliang;
    private String meiguzibengongjijin;
    private String stockCode;
    private String zhuyingyewushouru;
    private String zichanzongji;


    public String getCaiwufeiyong() {
        return caiwufeiyong;
    }

    public void setCaiwufeiyong(String caiwufeiyong) {
        this.caiwufeiyong = caiwufeiyong;
    }

    public String getChangqifuzaiheji() {
        return changqifuzaiheji;
    }

    public void setChangqifuzaiheji(String changqifuzaiheji) {
        this.changqifuzaiheji = changqifuzaiheji;
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

    public String getGudingzichanheji() {
        return gudingzichanheji;
    }

    public void setGudingzichanheji(String gudingzichanheji) {
        this.gudingzichanheji = gudingzichanheji;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJiezhiriqi() {
        return jiezhiriqi;
    }

    public void setJiezhiriqi(String jiezhiriqi) {
        this.jiezhiriqi = jiezhiriqi;
    }

    public String getJinlirun() {
        return jinlirun;
    }

    public void setJinlirun(String jinlirun) {
        this.jinlirun = jinlirun;
    }

    public String getLiudongzichanheji() {
        return liudongzichanheji;
    }

    public void setLiudongzichanheji(String liudongzichanheji) {
        this.liudongzichanheji = liudongzichanheji;
    }

    public String getMeigujinzichan() {
        return meigujinzichan;
    }

    public void setMeigujinzichan(String meigujinzichan) {
        this.meigujinzichan = meigujinzichan;
    }

    public String getMeigushouyi() {
        return meigushouyi;
    }

    public void setMeigushouyi(String meigushouyi) {
        this.meigushouyi = meigushouyi;
    }

    public String getMeiguxianjinhanliang() {
        return meiguxianjinhanliang;
    }

    public void setMeiguxianjinhanliang(String meiguxianjinhanliang) {
        this.meiguxianjinhanliang = meiguxianjinhanliang;
    }

    public String getMeiguzibengongjijin() {
        return meiguzibengongjijin;
    }

    public void setMeiguzibengongjijin(String meiguzibengongjijin) {
        this.meiguzibengongjijin = meiguzibengongjijin;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getZhuyingyewushouru() {
        return zhuyingyewushouru;
    }

    public void setZhuyingyewushouru(String zhuyingyewushouru) {
        this.zhuyingyewushouru = zhuyingyewushouru;
    }

    public String getZichanzongji() {
        return zichanzongji;
    }

    public void setZichanzongji(String zichanzongji) {
        this.zichanzongji = zichanzongji;
    }

    @Override
    public String toString() {
        return "ComapnyAnnualReportModel{" +
                "caiwufeiyong='" + caiwufeiyong + '\'' +
                ", changqifuzaiheji='" + changqifuzaiheji + '\'' +
                ", createDate=" + createDate +
                ", from='" + from + '\'' +
                ", gudingzichanheji='" + gudingzichanheji + '\'' +
                ", id='" + id + '\'' +
                ", jiezhiriqi='" + jiezhiriqi + '\'' +
                ", jinlirun='" + jinlirun + '\'' +
                ", liudongzichanheji='" + liudongzichanheji + '\'' +
                ", meigujinzichan='" + meigujinzichan + '\'' +
                ", meigushouyi='" + meigushouyi + '\'' +
                ", meiguxianjinhanliang='" + meiguxianjinhanliang + '\'' +
                ", meiguzibengongjijin='" + meiguzibengongjijin + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", zhuyingyewushouru='" + zhuyingyewushouru + '\'' +
                ", zichanzongji='" + zichanzongji + '\'' +
                '}';
    }
}
