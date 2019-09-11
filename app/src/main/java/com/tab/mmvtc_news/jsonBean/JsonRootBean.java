/**
  * Copyright 2019 bejson.com 
  */
package com.tab.mmvtc_news.jsonBean;
import java.util.List;

/**
 * Auto-generated: 2019-09-11 18:36:14
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class JsonRootBean {

    private int total;
    private double cost;
    private List<Content> content;
    private List<FacetsList> facetsList;
    private List<String> filters;
    public void setTotal(int total) {
         this.total = total;
     }
     public int getTotal() {
         return total;
     }

    public void setCost(double cost) {
         this.cost = cost;
     }
     public double getCost() {
         return cost;
     }

    public void setContent(List<Content> content) {
         this.content = content;
     }
     public List<Content> getContent() {
         return content;
     }

    public void setFacetsList(List<FacetsList> facetsList) {
         this.facetsList = facetsList;
     }
     public List<FacetsList> getFacetsList() {
         return facetsList;
     }

    public void setFilters(List<String> filters) {
         this.filters = filters;
     }
     public List<String> getFilters() {
         return filters;
     }

}