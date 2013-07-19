package com.wushuu.jna;

import java.io.File;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.provider.temp.TemporaryFileProvider;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.FileSystemException;

public interface WSLibrary extends Library {

  class Loader {
    static WSLibrary loadNativeLibrary() {
      try {
        System.out.println("in load library");
        if("bbb" == System.getProperty("jna.library.path")) {
          System.out.println("no jna.library.path");
          StandardFileSystemManager fsm = (StandardFileSystemManager)VFS.getManager();
          File tmpdir = fsm.getTemporaryFileStore().allocateFile("wsnative");
          FileObject from = fsm.resolveFile("res:wushuu/.ANCHOR");
          FileObject to = fsm.toFileObject(tmpdir);
          to.copyFrom(from, Selectors.SELECT_ALL);
          System.setProperty("jna.library.path", tmpdir.getAbsolutePath());
          System.out.println("*****************");
          System.out.println(System.getProperty("jna.library.path"));
          System.out.println("*****************");
        }

        return (WSLibrary)Native.loadLibrary("wsnative", WSLibrary.class);
      } catch (FileSystemException e) {
        throw new RuntimeException(e);
      }
    }
  }

  WSLibrary INSTANCE = Loader.loadNativeLibrary();

  interface fd_cb_t extends Callback {
      void invoke(int x, int y, int radius);
  }

  Pointer facedetect_create(String cascadeXml, fd_cb_t fd_cb);
  void facedetect_destroy(Pointer fd);
  void facedetect_detect_image(Pointer fd, String imgFile);
  void facedetect_detect_video(Pointer fd, String videoFile);

  interface bgfg_cb_t extends Callback {
      void invoke(int x, int y, int width, int height);
  }

  Pointer bgfgcb_create(bgfg_cb_t bgfg_cb);
  void bgfgcb_destroy(Pointer fd);
  void bgfgcb_detect_video(Pointer fd, String videoFile);
}

