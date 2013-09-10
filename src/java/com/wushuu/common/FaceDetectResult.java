package com.wushuu.common;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.BindBean;

public class FaceDetectResult {
  private String fileName;
  private int x;
  private int y;
  private int r;

  public FaceDetectResult(int x, int y, int r) {
    this.x = x;
    this.y = y;
    this.r = r;
  }
  public FaceDetectResult(String fileName, int x, int y, int r) {
    this(x, y, r);
    this.fileName = fileName;
  }

  public String getFileName() { return fileName; }
  public int getTopX() { return x - r; }
  public int getTopY() { return y - r; }
  public int getBottomX() { return x + r; }
  public int getBottomY() { return y + r; }

  public interface DAO {
    @SqlUpdate("insert into tbl_facial_regognition(file_name, top_x, top_y, bottom_x, bottom_y) values(:fileName, :topX, :topY, :bottomX, :bottomY)")
    public void insert(@BindBean FaceDetectResult fdr);

    public void close();
  }
}
