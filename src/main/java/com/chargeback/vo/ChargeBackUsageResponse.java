package com.chargeback.vo;

import java.util.Date;
/**
 * 
 * @author ambansal
 *
 */
public class ChargeBackUsageResponse {
	
	private double cpu;
	private long memory;
	private long disk;
	private Date time;
	private String orgName;
	private String appname;
	private String spaceName;
	private String instanceIndex;
	public double getCpu() {
		return cpu;
	}
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	public long getDisk() {
		return disk;
	}
	public void setDisk(long disk) {
		this.disk = disk;
	}
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getSpaceName() {
		return spaceName;
	}
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	public String getInstanceIndex() {
		return instanceIndex;
	}
	public void setInstanceIndex(String instanceIndex) {
		this.instanceIndex = instanceIndex;
	}
	
}
