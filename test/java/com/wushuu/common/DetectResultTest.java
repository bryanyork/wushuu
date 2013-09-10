package com.wushuu.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.skife.jdbi.v2.DBI;

@RunWith(JUnit4.class)
public class DetectResultTest {
  private DBI dbi = null;

  @Before
  public void setUp() {
    dbi = new DBI("jdbc:mysql://192.168.2.181/wushuu_acl?autoReconnect=true", "root", "woyoadmin");
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testBgFgInsert() {
    BgFgDetectResult.DAO dao = dbi.onDemand(BgFgDetectResult.DAO.class);
    dao.insert(new BgFgDetectResult("filename", 1, 2, 3, 4));
  }
}
