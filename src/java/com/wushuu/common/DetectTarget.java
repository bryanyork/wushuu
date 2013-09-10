package com.wushuu.common;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface DetectTarget {
  public class Bean {
    private String name;
    private String url;
  
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
  
    public String getUrl() {
      return url;
    }
    public void setUrl(String url) {
      this.url = url;
    }
  }

  @SqlQuery("select name, url from tbl_detect_target where not disabled")
  @MapResultAsBean
  public List<Bean> getAllEnabled();
}
