package com.wushuu;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

import  com.wushuu.spout.DirSpout;
import  com.wushuu.spout.FaceDetectSpout;
import  com.wushuu.spout.BgFgSpout;
import  com.wushuu.bolt.FaceDetectBolt;
import  com.wushuu.bolt.JDBCBolt;


public class Topology {
    
    public static void main(String[] args) throws Exception {
        
        TopologyBuilder builder = new TopologyBuilder();
        int dt = 2;
        if(0 == dt) {
          builder.setSpout("dir-spout", new DirSpout(), 1);
          builder.setBolt("fd-bolt", new FaceDetectBolt(), 2).shuffleGrouping("dir-spout");
          builder.setBolt("jdbc-bolt", new JDBCBolt(), 1).shuffleGrouping("fd-bolt");
        } else if(1 == dt) {
          builder.setSpout("fd-spout", new FaceDetectSpout(), 2);
          builder.setBolt("jdbc-bolt", new JDBCBolt(), 1).shuffleGrouping("fd-spout");
        } else if(2 == dt) {
          builder.setSpout("bgfg-spout", new BgFgSpout(), 2);
          builder.setBolt("jdbc-bolt", new JDBCBolt(), 1).shuffleGrouping("bgfg-spout");
        } else {
          throw new UnsupportedOperationException(); 
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
