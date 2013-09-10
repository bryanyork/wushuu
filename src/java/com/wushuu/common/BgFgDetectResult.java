package com.wushuu.common;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.mixins.CloseMe;

import com.google.common.base.Objects;

public class BgFgDetectResult {
  private String fileName;
  private int x;
  private int y;
  private int w;
  private int h;

  public BgFgDetectResult(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  public BgFgDetectResult(String fileName, int x, int y, int w, int h) {
    this(x, y, w, h);
    this.fileName = fileName;
  }

  public String getFileName() { return fileName; }
  public int getX() { return x; }
  public int getY() { return y; }
  public int getW() { return w; }
  public int getH() { return h; }

  public String toString() {
    return Objects.toStringHelper(this).add("fileName", fileName)
             .add("x", x).add("y", y).add("w", w).add("h", h).toString();
  }

  public interface DAO {
    @SqlUpdate("insert into tbl_security_event(file_name, top_x, top_y, bottom_x, bottom_y) values(:fileName, :x, :y, :w, :h)")
    public void insert(@BindBean BgFgDetectResult bfdr);
  }
}
