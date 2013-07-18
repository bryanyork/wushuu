package com.wushuu;

import com.wushuu.jna.WSLibrary;

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

public class SimpleSample {


  public static void main(String[] args) throws Exception {
    System.out.println("Enter to OpenCV");
    System.out.println(args.length);
    System.out.println(System.getProperty("java.library.path"));
    System.out.println(System.getProperty("jna.library.path"));

    if(null == System.getProperty("jna.library.path")) {
        StandardFileSystemManager fsm = (StandardFileSystemManager)VFS.getManager();
        File nf = fsm.getTemporaryFileStore().allocateFile("wsnatives");
        for(String s : fsm.getSchemes()) {
            System.out.println(s);
        }
        FileObject jar = fsm.resolveFile("res:native/wsnatives.jar");
        FileObject from = fsm.createFileSystem(jar);
        FileObject to = fsm.toFileObject(nf);
        to.copyFrom(from, Selectors.SELECT_ALL);
        System.setProperty("jna.library.path", nf.getAbsolutePath());
        //fsm.close();
    }

    WSLibrary.bgfg_cb_t bfcb = new WSLibrary.bgfg_cb_t() {
      public void invoke(int x, int y, int w, int h) {
        System.out.printf("from java, x=%d, y=%d, w=%d, h=%d", x, y, w, h);
        System.out.println();
      }
    };

    Pointer fd = WSLibrary.INSTANCE.bgfgcb_create(bfcb);
    WSLibrary.INSTANCE.bgfgcb_detect_video(fd, "tcp://192.168.2.168:9999");
    WSLibrary.INSTANCE.bgfgcb_destroy(fd);

    System.out.println("Leave to OpenCV");
  }

}
