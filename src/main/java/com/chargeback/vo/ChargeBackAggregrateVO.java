package com.chargeback.vo;

import java.util.List;

import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudSpace;

/**
 * 
 * @author ambansal
 *
 */
public class ChargeBackAggregrateVO {

	
	CloudApplication cloudApplication;

	ApplicationStats applicationStats;
	
	List<CloudSpace> spaces;

	public List<CloudSpace> getSpaces() {
		return spaces;
	}

	public void setSpaces(List<CloudSpace> spaces) {
		this.spaces = spaces;
	}

	public ApplicationStats getApplicationStats() {
		return applicationStats;
	}

	public void setApplicationStats(ApplicationStats applicationStats) {
		this.applicationStats = applicationStats;
	}

	public CloudApplication getCloudApplication() {
		return cloudApplication;
	}

	public void setCloudApplication(CloudApplication cloudApplication) {
		this.cloudApplication = cloudApplication;
	}
	
}
