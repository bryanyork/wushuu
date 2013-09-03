package com.wushuu.spout;

import com.wushuu.jna.WSLibrary;

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
  private Pointer p = null;

  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    WSLibrary.fd_cb_t fc = new WSLibrary.fd_cb_t() {
      public void invoke(int x, int y, int radius) {
        System.out.printf("from java, x=%d, y=%d, r=%d", x, y, radius);
      }
    };
    try {
      String xml = CharStreams.toString(new InputStreamReader(
                                ClassLoader.getSystemResourceAsStream("wushuu/data/lbpcascades/lbpcascade_frontalface.xml")));
      p = WSLibrary.INSTANCE.facedetect_create(xml, fc);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void nextTuple() {
  }

  //@Override
  //public void execute(Tuple tup, BasicOutputCollector collector) {
  //  WSLibrary.INSTANCE.facedetect_detect_image(p, tup.getStringByField("path"));
  //}

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("detect_type", "x", "y", "r"));
  }
}
