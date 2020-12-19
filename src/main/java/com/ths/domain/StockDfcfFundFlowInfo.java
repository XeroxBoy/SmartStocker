package com.ths.domain;

import java.math.BigDecimal;
import java.util.Date;

public class StockDfcfFundFlowInfo {

    /**
     * 主键
     */
    private Long id;

    /**
     * 股市名
     */
    private String stockMarket;

    /**
     * 排行榜名
     */
    private String stockRank;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名字
     */
    private String stockName;

    /**
     * 股票最新价格
     */
    private BigDecimal priceNew;

    /**
     * 涨跌幅
     */
    private BigDecimal stockChange;

    /**
     * 主力净流入--净额
     */
    private BigDecimal mainNetInflowAmount;

    /**
     * 主力净流入--净占比
     */
    private BigDecimal mainNetProportion;

    /**
     * 超大单净流入--净额
     */
    private BigDecimal superBigPartNetInflowAmount;

    /**
     * 超大单净流入--净占比
     */
    private BigDecimal superBigPartNetProportion;

    /**
     * 大单净流入--净额
     */
    private BigDecimal bigPartNetInflowAmount;

    /**
     * 大单净流入--净占比
     */
    private BigDecimal bigPartNetProportion;

    /**
     * 中单净流入--净额
     */
    private BigDecimal middlePartNetInflowAmount;

    /**
     * 中单净流入--净占比
     */
    private BigDecimal middlePartNetProportion;

    /**
     * 小单净流入--净额
     */
    private BigDecimal litterPartNetInflowAmount;

    /**
     * 小单净流入--净占比
     */
    private BigDecimal litterPartNetProportion;

    /**
     * 统计数据时间
     */
    private Date countTime;

    /**
     * 当前是第几页
     */
    private int stockPage;

    /**
     * 批次号
     */
    private String timeVersion;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 扩展数据
     */
    private String someinfo;

    private String crawlerVersion;

    private static final long serialVersionUID = 22222223213111L;

    public int getStockPage() {
        return stockPage;
    }

    public void setStockPage(int stockPage) {
        this.stockPage = stockPage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockMarket() {
        return stockMarket;
    }

    public void setStockMarket(String stockMarket) {
        this.stockMarket = stockMarket;
    }

    public String getStockRank() {
        return stockRank;
    }

    public void setStockRank(String stockRank) {
        this.stockRank = stockRank;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getPriceNew() {
        return priceNew;
    }

    public void setPriceNew(BigDecimal priceNew) {
        this.priceNew = priceNew;
    }

    public BigDecimal getStockChange() {
        return stockChange;
    }

    public void setStockChange(BigDecimal stockChange) {
        this.stockChange = stockChange;
    }

    public BigDecimal getMainNetInflowAmount() {
        return mainNetInflowAmount;
    }

    public void setMainNetInflowAmount(BigDecimal mainNetInflowAmount) {
        this.mainNetInflowAmount = mainNetInflowAmount;
    }

    public BigDecimal getMainNetProportion() {
        return mainNetProportion;
    }

    public void setMainNetProportion(BigDecimal mainNetProportion) {
        this.mainNetProportion = mainNetProportion;
    }

    public BigDecimal getSuperBigPartNetInflowAmount() {
        return superBigPartNetInflowAmount;
    }

    public void setSuperBigPartNetInflowAmount(BigDecimal superBigPartNetInflowAmount) {
        this.superBigPartNetInflowAmount = superBigPartNetInflowAmount;
    }

    public BigDecimal getSuperBigPartNetProportion() {
        return superBigPartNetProportion;
    }

    public void setSuperBigPartNetProportion(BigDecimal superBigPartNetProportion) {
        this.superBigPartNetProportion = superBigPartNetProportion;
    }

    public BigDecimal getBigPartNetInflowAmount() {
        return bigPartNetInflowAmount;
    }

    public void setBigPartNetInflowAmount(BigDecimal bigPartNetInflowAmount) {
        this.bigPartNetInflowAmount = bigPartNetInflowAmount;
    }

    public BigDecimal getBigPartNetProportion() {
        return bigPartNetProportion;
    }

    public void setBigPartNetProportion(BigDecimal bigPartNetProportion) {
        this.bigPartNetProportion = bigPartNetProportion;
    }

    public BigDecimal getMiddlePartNetInflowAmount() {
        return middlePartNetInflowAmount;
    }

    public void setMiddlePartNetInflowAmount(BigDecimal middlePartNetInflowAmount) {
        this.middlePartNetInflowAmount = middlePartNetInflowAmount;
    }

    public BigDecimal getMiddlePartNetProportion() {
        return middlePartNetProportion;
    }

    public void setMiddlePartNetProportion(BigDecimal middlePartNetProportion) {
        this.middlePartNetProportion = middlePartNetProportion;
    }

    public BigDecimal getLitterPartNetInflowAmount() {
        return litterPartNetInflowAmount;
    }

    public void setLitterPartNetInflowAmount(BigDecimal litterPartNetInflowAmount) {
        this.litterPartNetInflowAmount = litterPartNetInflowAmount;
    }

    public BigDecimal getLitterPartNetProportion() {
        return litterPartNetProportion;
    }

    public void setLitterPartNetProportion(BigDecimal litterPartNetProportion) {
        this.litterPartNetProportion = litterPartNetProportion;
    }

    public Date getCountTime() {
        return countTime;
    }

    public void setCountTime(Date countTime) {
        this.countTime = countTime;
    }

    public String getTimeVersion() {
        return timeVersion;
    }

    public void setTimeVersion(String timeVersion) {
        this.timeVersion = timeVersion;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSomeinfo() {
        return someinfo;
    }

    public void setSomeinfo(String someinfo) {
        this.someinfo = someinfo;
    }

    public String getCrawlerVersion() {
        return crawlerVersion;
    }

    public void setCrawlerVersion(String crawlerVersion) {
        this.crawlerVersion = crawlerVersion;
    }

}
