package com.wushuu.bolt;

import com.wushuu.jna.WSLibrary;
import com.wushuu.common.FaceDetectResult;

import java.util.Map;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

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
  private BlockingQueue resultQueue = new ArrayBlockingQueue<FaceDetectResult>(1024);

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
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
  public void execute(Tuple tup, BasicOutputCollector collector) {
    WSLibrary.INSTANCE.facedetect_detect_image(p, tup.getStringByField("path"));
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("detect_type", "x", "y", "r"));
  }
}
