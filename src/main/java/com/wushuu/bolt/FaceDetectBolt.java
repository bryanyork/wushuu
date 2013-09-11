package com.wushuu.bolt;

import com.wushuu.jna.WSLibrary;
import com.wushuu.common.FaceDetectResult;
import com.wushuu.common.DetectType;

import java.util.Map;
import java.io.InputStreamReader;
import java.io.IOException;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.sun.jna.Pointer;

import com.google.common.io.CharStreams;


public class FaceDetectBolt extends BaseBasicBolt {
  private Pointer p = null;

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    WSLibrary.fd_cb_t fc = new WSLibrary.fd_cb_t() {
      public void invoke(int x, int y, int radius) {
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
  public void execute(Tuple tup, BasicOutputCollector collector) {
    final BasicOutputCollector coll = collector;
    final String file_path = tup.getStringByField("file_path");
    WSLibrary.fd_cb_t fc = new WSLibrary.fd_cb_t() {
      public void invoke(int x, int y, int radius) {
          System.out.println("+++++++++++++++++++++++++++++++++");
          System.out.printf("from java, x=%d, y=%d, r=%d", x, y, radius);
          System.out.println("+++++++++++++++++++++++++++++++++");
          coll.emit(new Values(DetectType.FACE_DETECT, new FaceDetectResult(file_path, x, y, radius)));
      }
    };
    WSLibrary.INSTANCE.facedetect_detect_image(p, file_path, fc);
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("detect_type", "detect_result"));
  }
}
