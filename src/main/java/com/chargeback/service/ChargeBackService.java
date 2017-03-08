package com.chargeback.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chargeback.entity.Usage;
import com.chargeback.entity.UsageSummary;
import com.chargeback.vo.ChargeBackAggregrateVO;
import com.chargeback.vo.ChargeBackUsageResponse;

public interface ChargeBackService {

	List<ChargeBackUsageResponse> getChargeBackUsage(final List<ChargeBackAggregrateVO> chargeBackAggregrateVOs);

	void persistUsageData(Usage usage);
	
	public Map<String, List<Usage>> getUsageDataBetweenDates(final Date fromDate , final Date toDate, final String orgName);
	
	void persistUsageSummaryData(UsageSummary usageSummary);
	

}
