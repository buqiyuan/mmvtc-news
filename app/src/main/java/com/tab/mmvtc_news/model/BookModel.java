package com.tab.mmvtc_news.model;

/**
 * 图书实体类
 */
public class BookModel {
    /**
     * 分类号
     */
    private String classification;
    /**
     * 书名
     */
    private String bookName;
    /**
     * 作者
     */
    private String author;
    /**
     * 作者
     */
    private String bookPublisher;
    /**
     * 馆藏总数
     */
    private String totalNumber;
    /**
     * 馆藏可借数量
     */
    private String remainNumber;
    /**
     * 详情页url
     */
    private String urlDetail;

    public BookModel(String classification, String bookName, String author, String urlDetail) {
        this.classification = classification;
        this.bookName = bookName;
        this.author = author;
        this.urlDetail = urlDetail;
    }

    public BookModel(String bookPublisher, String bookName, String author, String urlDetail, String totalNumber, String remainNumber) {
        this.bookPublisher = bookPublisher;
        this.bookName = bookName;
        this.author = author;
        this.urlDetail = urlDetail;
        this.totalNumber = totalNumber;
        this.remainNumber = remainNumber;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrlDetail() {
        return urlDetail;
    }

    public void setUrlDetail(String urlDetail) {
        this.urlDetail = urlDetail;
    }

    public String getRremainNumber() {
        return remainNumber;
    }

    public String getTotalNumber() {
        return totalNumber;
    }
    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setTotalNumber(String totalNumber) {
        this.totalNumber = totalNumber;
    }

    public void setRemainNumber(String remainNumber) {
        this.remainNumber = remainNumber;
    }
}
