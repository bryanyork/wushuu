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
  private String dbString = null;
  private String dbUser = null;
  private String dbPass = null;
  private String connectionString = null;

  private DBI  dbi = null;

  public JDBCBolt(String dbString, String dbUser, String dbPass) {
    this.dbString = dbString;
    this.dbUser   = dbUser;
    this.dbPass   = dbPass;
  }

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    dbi = new DBI(dbString, dbUser, dbPass);
  }

  @Override
  public void execute(Tuple tup, BasicOutputCollector collector) {
    DetectType dt = (DetectType)tup.getValueByField("detect_type");
    System.out.printf("*****from java, %s, %s", dt, tup.getValueByField("detect_result"));
    if(DetectType.FACE_DETECT == dt) {
      FaceDetectResult.DAO faceDetectDAO = dbi.onDemand(FaceDetectResult.DAO.class);
      faceDetectDAO.insert( (FaceDetectResult)tup.getValueByField("detect_result") );
    } else if(DetectType.BGFG_DETECT == dt) {
      BgFgDetectResult dr = (BgFgDetectResult)tup.getValueByField("detect_result");
      BgFgDetectResult.DAO dao = dbi.onDemand(BgFgDetectResult.DAO.class);
      dao.insert( (BgFgDetectResult)tup.getValueByField("detect_result") );
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
  }
}
