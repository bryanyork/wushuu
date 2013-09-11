package com.wushuu.common;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.BindBean;
import com.google.common.base.Objects;

public class BgFgDetectResult extends AbstractDetectResult {
  private int x;
  private int y;
  private int w;
  private int h;

  public BgFgDetectResult(String fileName, int x, int y, int w, int h) {
    super(fileName);
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public int getX() { return x; }
  public int getY() { return y; }
  public int getW() { return w; }
  public int getH() { return h; }

  protected Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper().add("x", x).add("y", y)
              .add("w", w).add("h", h);
  }

  public interface DAO {
    @SqlUpdate("insert into tbl_security_event(file_name, detect_time, top_x, top_y, bottom_x, bottom_y) values(:fileName, :detectDateTime, :x, :y, :w, :h)")
    public void insert(@BindBean BgFgDetectResult bfdr);
  }
}
