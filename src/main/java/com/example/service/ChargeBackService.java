package com.example.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.entity.Usage;
import com.example.entity.UsageSummary;
import com.example.vo.ChargeBackAggregrateVO;
import com.example.vo.ChargeBackUsageResponse;

public interface ChargeBackService {

	List<ChargeBackUsageResponse> getChargeBackUsage(final List<ChargeBackAggregrateVO> chargeBackAggregrateVOs);

	void persistUsageData(Usage usage);
	
	public Map<String, List<Usage>> getUsageDataBetweenDates(final Date fromDate , final Date toDate, final String orgName);
	
	void persistUsageSummaryData(UsageSummary usageSummary);
	

}
