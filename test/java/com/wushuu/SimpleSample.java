package com.wushuu;

import com.wushuu.jna.WSLibrary;

import java.io.File;
import java.io.InputStreamReader;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import com.google.common.io.CharStreams;

public class SimpleSample {


  public static void main(String[] args) throws Exception {
    System.out.println("Enter to OpenCV");
    System.out.println(args.length);

    if(false) {
      WSLibrary.bgfg_cb_t bfcb = new WSLibrary.bgfg_cb_t() {
        public void invoke(int x, int y, int w, int h) {
          System.out.printf("from java, x=%d, y=%d, w=%d, h=%d", x, y, w, h);
          System.out.println();
        }
      };

      Pointer ws = WSLibrary.INSTANCE.bgfgcb_create(bfcb);
      WSLibrary.INSTANCE.bgfgcb_detect_video(ws, "tcp://127.0.0.1:9876");
      WSLibrary.INSTANCE.bgfgcb_destroy(ws);
    } else {
      WSLibrary.fd_cb_t fc = new WSLibrary.fd_cb_t() {
        public void invoke(int x, int y, int radius) {
          System.out.printf("from java, x=%d, y=%d, r=%d", x, y, radius);
          System.out.println();
        }
      };

      String xml = CharStreams.toString(new InputStreamReader(
                                ClassLoader.getSystemResourceAsStream("wushuu/data/lbpcascades/lbpcascade_frontalface.xml")));

      Pointer ws = WSLibrary.INSTANCE.facedetect_create(xml, fc);
      WSLibrary.INSTANCE.facedetect_detect_video(ws, "tcp://127.0.0.1:9876");
      //WSLibrary.INSTANCE.facedetect_detect_image(ws, "/home/jamesf/os/opencv-2.4.6/samples/c/lena.jpg");
      WSLibrary.INSTANCE.facedetect_destroy(ws);
    }

    System.out.println("Leave to OpenCV");
  }

}
