package com.chargeback.vo;

import java.util.List;

import org.cloudfoundry.client.lib.domain.CloudSpace;

public class ChargeBackInputVO {

	List<ChargeBackAggregrateVO> chargeBackAggregrateVOs;
	List<CloudSpace> cloudSpaces;
	public List<ChargeBackAggregrateVO> getChargeBackAggregrateVOs() {
		return chargeBackAggregrateVOs;
	}
	public void setChargeBackAggregrateVOs(List<ChargeBackAggregrateVO> chargeBackAggregrateVOs) {
		this.chargeBackAggregrateVOs = chargeBackAggregrateVOs;
	}
	public List<CloudSpace> getCloudSpaces() {
		return cloudSpaces;
	}
	public void setCloudSpaces(List<CloudSpace> cloudSpaces) {
		this.cloudSpaces = cloudSpaces;
	}
	
}
