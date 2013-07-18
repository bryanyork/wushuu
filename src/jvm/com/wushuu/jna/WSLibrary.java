package com.wushuu.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface WSLibrary extends Library {
  WSLibrary INSTANCE = (WSLibrary)Native.loadLibrary("wsfd", WSLibrary.class);

  interface fd_cb_t extends Callback {
      void invoke(int x, int y, int radius);
  }

  Pointer facedetect_create(String cascadeXml, fd_cb_t fd_cb);
  void facedetect_destroy(Pointer fd);
  void facedetect_detect_image(Pointer fd, String imgFile);

  interface bgfg_cb_t extends Callback {
      void invoke(int x, int y, int width, int height);
  }

  Pointer bgfgcb_create(bgfg_cb_t bgfg_cb);
  void bgfgcb_destroy(Pointer fd);
  void bgfgcb_detect_video(Pointer fd, String videoFile);
}

