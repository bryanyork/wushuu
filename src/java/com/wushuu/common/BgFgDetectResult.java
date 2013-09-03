package com.wushuu.common;

public class BgFgDetectResult {
  public String file_path;
  public int x;
  public int y;
  public int w;
  public int h;

  public BgFgDetectResult(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public BgFgDetectResult(String file_path, int x, int y, int w, int h) {
    this(x, y, w, h);
    this.file_path = file_path;
  }
}
