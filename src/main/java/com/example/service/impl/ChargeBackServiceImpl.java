package com.example.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Usage;
import com.example.entity.UsageSummary;
import com.example.repository.UsageRepository;
import com.example.repository.UsageSummaryRepository;
import com.example.service.ChargeBackService;
import com.example.vo.ChargeBackAggregrateVO;
import com.example.vo.ChargeBackUsageResponse;

/**
 * 
 * @author ambansal
 *
 */
@Service
public class ChargeBackServiceImpl implements ChargeBackService {

	@Autowired
	private UsageRepository usageRepository;
	
	@Autowired
	private UsageSummaryRepository usageSummaryRepository;
	
	final Function<ChargeBackAggregrateVO, List<ChargeBackUsageResponse>> maptoUsage 
    = new Function<ChargeBackAggregrateVO, List<ChargeBackUsageResponse>>() {
    
    public List<ChargeBackUsageResponse> apply(final ChargeBackAggregrateVO chargeBackAggregrateVO) {
       
    	final List<ChargeBackUsageResponse> backUsageResponseList =  new ArrayList<>();
    	for(InstanceStats stats: chargeBackAggregrateVO.getApplicationStats().getRecords()){
    		final ChargeBackUsageResponse chargeBackUsageResponse = new ChargeBackUsageResponse();
    		chargeBackUsageResponse.setAppname(stats.getName());
    		chargeBackUsageResponse.setCpu(stats.getUsage().getCpu());
    		chargeBackUsageResponse.setDisk(stats.getUsage().getDisk());
    		chargeBackUsageResponse.setInstanceIndex(stats.getId());
    		chargeBackUsageResponse.setMemory(stats.getUsage().getMem());
    		chargeBackUsageResponse.setTime(stats.getUsage().getTime());
    		chargeBackUsageResponse.setSpaceName(chargeBackAggregrateVO.getCloudApplication().getSpace().getName());
    		String orgName = chargeBackAggregrateVO.getSpaces().stream().filter(cloudSpace -> (cloudSpace.getName().equals(chargeBackAggregrateVO.getCloudApplication().getSpace().getName())))
    		.map(cloudSpace -> cloudSpace.getOrganization().getName()).collect(Collectors.toList()).get(0);
    		chargeBackUsageResponse.setOrgName(orgName);
    		backUsageResponseList.add(chargeBackUsageResponse);
    	}
    	
    	
        return backUsageResponseList;
    }
};
	@Override
	public List<ChargeBackUsageResponse> getChargeBackUsage(final List<ChargeBackAggregrateVO> chargeBackAggregrateVOs) {
		final List<ChargeBackUsageResponse> chargeBackUsageResponses =  new ArrayList<>();
		chargeBackAggregrateVOs.stream().map(maptoUsage).collect(Collectors.toList()).stream().flatMap(records -> records.stream()).forEach(chargeBackUsageResponses::add);
		return chargeBackUsageResponses;
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void persistUsageData(Usage usage) {
		usageRepository.save(usage);	
	}

	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void persistUsageSummaryData(UsageSummary usageSummary) {
		usageSummaryRepository.save(usageSummary);	
		
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public Map<String, List<Usage>> getUsageDataBetweenDates(final Date fromDate , final Date toDate, final String orgName){
		final Map<String, List<Usage>> summaryUsageMap = new HashMap<>();
			final List<String> spaceList = usageRepository.findDistinctSpaceName(fromDate, toDate, orgName);
			for(final String space: spaceList){
				final List<String> appNameList = usageRepository.findDistinctApps(fromDate, toDate, orgName, space);
						for(final String appname : appNameList){
							final List<Integer> instanceIndexList = usageRepository.findIndexesforApp(fromDate, toDate, orgName, space, appname);
							for(final Integer index : instanceIndexList){
								final List<Usage> usagePerAppPerInstance = usageRepository.findByDateAndNameAndApplication(fromDate, toDate, orgName, space, appname, index);
								summaryUsageMap.put(orgName+space+appname+index, usagePerAppPerInstance);
							}
						}
					}
		return summaryUsageMap;
	}
	
	
}



