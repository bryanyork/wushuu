package com.wushuu;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.ShellBolt;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.opencv.core.Core;

public class Topology {
    
    public static void main(String[] args) throws Exception {
        
        TopologyBuilder builder = new TopologyBuilder();
        
        //builder.setSpout("spout", new RandomSentenceSpout(), 5);
        

        Config conf = new Config();
        conf.setDebug(true);

        if(args!=null && args.length > 0) {
            conf.setNumWorkers(3);
            
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {        
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());
        
            Thread.sleep(10000);

            cluster.shutdown();
        }
    }
}
