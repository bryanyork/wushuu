package com.wushuu.common;

public class FaceDetectResult {
  public String file_path;
  public int x;
  public int y;
  public int r;

  public FaceDetectResult(int x, int y, int r) {
    this.x = x;
    this.y = y;
    this.r = r;
  }

  public FaceDetectResult(String file_path, int x, int y, int r) {
    this(x, y, r);
    this.file_path = file_path;
  }
}
