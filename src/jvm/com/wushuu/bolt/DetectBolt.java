package com.wushuu.bolt;

import java.util.Map;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;

import com.sun.jna.Pointer;

import com.wushuu.jna.WSLibrary;

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
    p = WSLibrary.INSTANCE.facedetect_create("/home/jamesf/work/wushuu/res/data/haarcascades/haarcascade_frontalface_default.xml", fc);
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
