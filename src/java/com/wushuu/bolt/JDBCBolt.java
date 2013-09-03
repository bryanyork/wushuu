package com.wushuu.bolt;

import java.util.Map;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;

public class JDBCBolt extends BaseBasicBolt {
  Connection  mysql = null;

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    try {
      mysql = DriverManager.getConnection("jdbc:mysql://192.168.2.181/wushuu_demo?autoReconnect=true", "root", "woyoadmin");
    } catch (SQLException e) {
    }
  }

  @Override
  public void execute(Tuple tup, BasicOutputCollector collector) {
    System.out.println(tup);
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
  }
}
