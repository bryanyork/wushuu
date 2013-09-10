package com.wushuu.common;

import java.util.Date;
import java.sql.Timestamp;

import com.google.common.base.Objects;

abstract class AbstractDetectResult {
  private String fileName;
  private Date detectDateTime;

  public AbstractDetectResult(String fileName) {
    this.fileName = fileName;
    this.detectDateTime = new Date();
  }

  public String getFileName() { return fileName; }
  public Date getDetectDateTime() { return detectDateTime; }

  protected Objects.ToStringHelper toStringHelper() {
    return Objects.toStringHelper(this).add("fileName", fileName)
             .add("detectDateTime", detectDateTime);
  }

  public String toString() {
    return toStringHelper().toString();
  }
}
