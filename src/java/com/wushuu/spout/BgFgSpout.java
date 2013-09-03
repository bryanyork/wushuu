package com.wushuu.spout;

import com.wushuu.jna.WSLibrary;
import com.wushuu.common.BgFgDetectResult;
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

public class BgFgSpout extends BaseRichSpout {
  private Object event = null;

  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    this.event = new Object();
    final SpoutOutputCollector coll= collector;
    final WSLibrary.bgfg_cb_t bgfg = new WSLibrary.bgfg_cb_t() {
      public void invoke(int x, int y, int w, int h) {
        try {
          synchronized(event) {
            event.wait();
          }
        } catch(InterruptedException e) {}
        System.out.printf("from java, x=%d, y=%d, w=%d, h=%d", x, y, w, h);
        coll.emit(new Values(DetectType.BGFG_DETECT, new BgFgDetectResult("tcp://127.0.0.1:8899", x, y, w, h)));
      }
    };

    new Thread(new Runnable() {
      public void run () {
        Pointer p = WSLibrary.INSTANCE.bgfgcb_create(bgfg);
        WSLibrary.INSTANCE.bgfgcb_detect_video(p, "tcp://127.0.0.1:8899");
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
