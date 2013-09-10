package com.wushuu;

import java.util.Map;
import java.util.List;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.BoltDeclarer;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import com.wushuu.spout.DirSpout;
import com.wushuu.spout.FaceDetectSpout;
import com.wushuu.spout.BgFgSpout;
import com.wushuu.bolt.FaceDetectBolt;
import com.wushuu.bolt.JDBCBolt;
import com.wushuu.common.DetectTarget;

public class Demo {
    
    public static void main(String[] args) throws Exception { TopologyBuilder
    builder = new TopologyBuilder();

        DBI mysql = new DBI("jdbc:mysql://192.168.2.181/wushuu_acl?autoReconnect=true", "root",
        "woyoadmin");

        DetectTarget.DAO dao = mysql.onDemand(DetectTarget.DAO.class);
        List<DetectTarget> dts = dao.getAllEnabled();
        for(DetectTarget dt : dts) {
          builder.setSpout(dt.getName(), new BgFgSpout(dt), 1);
        }

        BoltDeclarer bd = builder.setBolt("jdbc-bolt",
                        new JDBCBolt("jdbc:mysql://192.168.2.181/wushuu_demo?autoReconnect=true", "root", "woyoadmin"),
                        1);
        for(DetectTarget dt : dts) {
          bd.shuffleGrouping(dt.getName());
        }
        
        Config conf = new Config();
        conf.setDebug(true);

        System.out.println("entering main");

        if(args!=null && args.length > 0) {
            System.out.println("submit topology");
            conf.setNumWorkers(4);
            
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {        
            System.out.println("local cluster");
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("wushuu-detect", conf, builder.createTopology());
        
            while(true) {
                Thread.sleep(1000);
            }
        }
    }
}
