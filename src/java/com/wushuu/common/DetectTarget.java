package com.wushuu.common;

import java.util.List;
import java.io.Serializable;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public class DetectTarget implements Serializable {
  private String name;
  private String url;

  public DetectTarget() {}
  public DetectTarget(String name, String url) {
    this.name = name;
    this.url  = url;
  }
  
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

  public interface DAO {
    @SqlQuery("select name, url from tbl_detect_target where not disabled")
    @MapResultAsBean
    public List<DetectTarget> getAllEnabled();
  }
}
