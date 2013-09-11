package com.wushuu.bolt;

import com.wushuu.common.FaceDetectResult;
import com.wushuu.common.BgFgDetectResult;
import com.wushuu.common.DetectType;

import java.util.Map;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;

import org.skife.jdbi.v2.DBI;

public class JDBCBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 3503945284591246373L;
	
	private String dbString = null;
  private String dbUser = null;
  private String dbPass = null;
  private DBI  dbi = null;

  public JDBCBolt(String dbString, String dbUser, String dbPass) {
    this.dbString = dbString;
    this.dbUser   = dbUser;
    this.dbPass   = dbPass;
  }

  @Override
  public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context) {
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
