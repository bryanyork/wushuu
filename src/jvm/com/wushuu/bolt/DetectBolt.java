package com.wushuu.bolt;

import com.wushuu.jna.WSLibrary;

import java.util.Map;
import java.io.InputStreamReader;
import java.io.IOException;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;

import com.sun.jna.Pointer;

import com.google.common.io.CharStreams;

public class DetectBolt extends BaseBasicBolt {
  private Pointer p = null;

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    WSLibrary.fd_cb_t fc = new WSLibrary.fd_cb_t() {
      public void invoke(int x, int y, int radius) {
        System.out.printf("from java, x=%d, y=%d, r=%d", x, y, radius);
        System.out.println();
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
    System.out.println(tup);

    WSLibrary.INSTANCE.facedetect_detect_image(p, tup.getStringByField("path"));
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
  }
}
