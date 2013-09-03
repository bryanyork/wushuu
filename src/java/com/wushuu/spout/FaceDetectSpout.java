package com.wushuu.spout;

import com.wushuu.jna.WSLibrary;
import com.wushuu.common.FaceDetectResult;
import com.wushuu.common.DetectType;

import java.util.Map;
import java.io.InputStreamReader;
import java.io.IOException;

import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.sun.jna.Pointer;

import com.google.common.io.CharStreams;

public class FaceDetectSpout extends BaseRichSpout {
  private Object event = null;

  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    this.event = new Object();
    final SpoutOutputCollector coll= collector;
    final WSLibrary.fd_cb_t fc = new WSLibrary.fd_cb_t() {
      public void invoke(int x, int y, int radius) {
        try {
          synchronized(event) {
            event.wait();
          }
        } catch(InterruptedException e) {}
        System.out.printf("from java, x=%d, y=%d, r=%d", x, y, radius);
        coll.emit(new Values(DetectType.FACE_DETECT, new FaceDetectResult("tcp://127.0.0.1:8899", x, y, radius)));
      }
    };

    new Thread(new Runnable() {
      public void run () {
        try {
          String xml = CharStreams.toString(new InputStreamReader(
                                    ClassLoader.getSystemResourceAsStream("wushuu/data/lbpcascades/lbpcascade_frontalface.xml")));
          Pointer p = WSLibrary.INSTANCE.facedetect_create(xml, fc);
          WSLibrary.INSTANCE.facedetect_detect_video(p, "tcp://127.0.0.1:8899");
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  @Override
  public void nextTuple() {
    synchronized(event) {
      event.notify();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("detect_type", "detect_result"));
  }
}
