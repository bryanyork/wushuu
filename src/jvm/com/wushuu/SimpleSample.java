package com.wushuu;

import java.io.File;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Callback;

import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.provider.temp.TemporaryFileProvider;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;

public class SimpleSample {

  public interface FDLibrary extends Library {
      FDLibrary INSTANCE = (FDLibrary) Native.loadLibrary("wsfd", FDLibrary.class);

      void test1();
      void test2(int argc, String[] argv);

      interface fd_cb extends Callback {
          void invoke(int x, int y, int radius);
      }

      void set_cb(fd_cb fc);
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Enter to OpenCV");
    System.out.println(args.length);
    System.out.println(args[0]);
    System.out.println(System.getProperty("java.library.path"));
    System.out.println(System.getProperty("jna.library.path"));
    if(null == System.getProperty("jna.library.path")) {
        StandardFileSystemManager fsm = (StandardFileSystemManager)VFS.getManager();
        File nf = fsm.getTemporaryFileStore().allocateFile("wsnatives");
        System.out.println("&&&&&&&&&&");
        System.out.println(nf);
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
    FDLibrary.INSTANCE.test1();

    FDLibrary.fd_cb fc = new FDLibrary.fd_cb() {
        public void invoke(int x, int y, int radius) {
            System.out.printf("from java, x=%d, y=%d, r=%d", x, y, radius);
            System.out.println();
        }
    };
    FDLibrary.INSTANCE.set_cb(fc);

    FDLibrary.INSTANCE.test2(args.length, args);
    System.out.println("Leave to OpenCV");
  }

}
