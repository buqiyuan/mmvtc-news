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
public class FacetsList {

    private List<FacetList> facetList;
    private String id;
    private String label;
    public void setFacetList(List<FacetList> facetList) {
         this.facetList = facetList;
     }
     public List<FacetList> getFacetList() {
         return facetList;
     }

    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setLabel(String label) {
         this.label = label;
     }
     public String getLabel() {
         return label;
     }

}