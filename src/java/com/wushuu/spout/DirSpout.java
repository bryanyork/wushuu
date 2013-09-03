package com.wushuu.spout;

import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;

import backtype.storm.task.TopologyContext;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import static com.google.common.io.Files.getFileExtension;

public class DirSpout extends  BaseRichSpout {
  private Object event = null;

  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    event = new Object();
    final SpoutOutputCollector coll= collector;
    new Thread(new Runnable() {
      public void run () {
        Path dir = Paths.get("/opt/wolftau");
        try {
          Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
              try {
                synchronized(event) {
                  event.wait();
                }
              } catch(InterruptedException e) {}
              String p = file.toFile().getCanonicalPath();
              if(0 == getFileExtension(p).compareToIgnoreCase("jpg")) {
                coll.emit(new Values(file.toFile().getCanonicalPath()));
              }
              return FileVisitResult.CONTINUE;
            }
          });
        } catch(IOException e) {}
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
    declarer.declare(new Fields("file_path"));
  }

}
