package com.tab.mmvtc_news.model;

import java.util.List;

public class SearchResult {
    //注意变量名与字段名一致
    private int total;
    private String msg;
    private List<Content> contentList;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public static class Content {
        private String author;
        private String callNo;
        private String docTypeName;
        private String isbn;
        private String marcRecNo;
        private String num;
        private String pubYear;
        private String publisher;
        private String title;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getCallNo() {
            return callNo;
        }

        public void setCallNo(String callNo) {
            this.callNo = callNo;
        }

        public String getDocTypeName() {
            return docTypeName;
        }

        public void setDocTypeName(String docTypeName) {
            this.docTypeName = docTypeName;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getMarcRecNo() {
            return marcRecNo;
        }

        public void setMarcRecNo(String marcRecNo) {
            this.marcRecNo = marcRecNo;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getPubYear() {
            return pubYear;
        }

        public void setPubYear(String pubYear) {
            this.pubYear = pubYear;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}