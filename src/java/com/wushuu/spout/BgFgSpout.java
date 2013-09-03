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

public class BgFgSpout extends BaseRichSpout {
  private Pointer p = null;

  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    WSLibrary.bgfg_cb_t bgfg = new WSLibrary.bgfg_cb_t() {
      public void invoke(int x, int y, int w, int h) {
        System.out.printf("from java, x=%d, y=%d, w=%d, h=%d", x, y, w, h);
      }
    };
    p = WSLibrary.INSTANCE.bgfgcb_create(bgfg);
  }

  //@Override
  //public void execute(Tuple tup, BasicOutputCollector collector) {
  //  WSLibrary.INSTANCE.bgfgcb_detect_video(p, tup.getStringByField("path"));
  //}
  @Override
  public void nextTuple() {
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("detect_type", "x", "y", "w", "h"));
  }
}
