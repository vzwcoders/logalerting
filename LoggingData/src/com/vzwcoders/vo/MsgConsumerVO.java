package com.vzwcoders.vo;

public class MsgConsumerVO {
	public String status;
	public String startTime;
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public int msgReceiveCount;
	public int dbInsertCount;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getMsgReceiveCount() {
		return msgReceiveCount;
	}
	public void setMsgReceiveCount(int msgReceiveCount) {
		this.msgReceiveCount = msgReceiveCount;
	}
	public int getDbInsertCount() {
		return dbInsertCount;
	}
	public void setDbInsertCount(int dbInsertCount) {
		this.dbInsertCount = dbInsertCount;
	}
	
}
