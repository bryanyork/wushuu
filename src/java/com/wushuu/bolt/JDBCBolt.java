package com.wushuu.bolt;

import com.wushuu.common.FaceDetectResult;
import com.wushuu.common.BgFgDetectResult;
import com.wushuu.common.DetectType;

import java.util.Map;
import java.sql.SQLException;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class JDBCBolt extends BaseBasicBolt {
  DBI  mysql = null;

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    mysql = new DBI("jdbc:mysql://192.168.2.181/wushuu_demo?autoReconnect=true", "root", "woyoadmin");
  }

  @Override
  public void execute(Tuple tup, BasicOutputCollector collector) {
    try (
      Handle h = mysql.open();
    ) {
      DetectType dt = (DetectType)tup.getValueByField("detect_type");
      if(DetectType.FACE_DETECT == dt) {
        FaceDetectResult fdr = (FaceDetectResult)tup.getValueByField("detect_result");
        h.insert("insert into tbl_facial_regognition(file_name, top_x, top_y, bottom_x, bottom_y) values(?, ?, ?, ?, ?)",
                 fdr.file_path, fdr.x - fdr.r, fdr.y - fdr.r, fdr.x + fdr.x, fdr.y + fdr.r);
      } else if(DetectType.BGFG_DETECT == dt) {
        BgFgDetectResult bfdr = (BgFgDetectResult)tup.getValueByField("detect_result");
        h.insert("insert into tbl_security_event(file_name, top_x, top_y, bottom_x, bottom_y) values(?, ?, ?, ?, ?)",
                 bfdr.file_path, bfdr.x, bfdr.y, bfdr.w, bfdr.h);
      } else {
      }
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
  }
}
