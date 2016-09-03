package com.example.controller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Usage;
import com.example.entity.UsageSummary;
import com.example.service.ChargeBackService;
import com.example.vo.ChargeBackAggregrateVO;
import com.example.vo.ChargeBackUsageResponse;

@RestController
public class CFMetricsController {
	private Log log = LogFactory.getLog(CFMetricsController.class);

	@Autowired
	private ChargeBackService chargebackService;

	/**
	 * This Controller fetches the free available resource based on the Resource
	 * Type at the Org Level
	 * 
	 * @param resourceType
	 * @return
	 */

	@RequestMapping(value = "/getFreeResource/{resourceType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getFreeResource(@PathVariable String resourceType) {
		log.info(String.format("getFreeResource %s", resourceType));
		CloudFoundryClient client = loginCloudFoundry();

		if (resourceType.equals("MEM")) {
			long memoryquota = client.getQuotaByName("default", true).getMemoryLimit() * 1024 * 1024;

			for (CloudApplication application : client.getApplications()) {
				memoryquota = memoryquota
						- ((client.getApplicationStats(application.getName()).getRecords().get(0).getUsage().getMem()));
			}
			return String.valueOf(memoryquota);
		} else if (resourceType.equals("CPU")) {
			double cpuQuota = 1.0;
			for (CloudApplication application : client.getApplications()) {
				cpuQuota = cpuQuota
						- ((client.getApplicationStats(application.getName()).getRecords().get(0).getUsage().getCpu()));
			}
			return String.valueOf(cpuQuota);
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/getInstanceMetrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ChargeBackUsageResponse> getApplicationInstancesData() {
		log.info(String.format("getInstanceMetrics"));

		CloudFoundryClient client = loginCloudFoundry();
		final List<CloudSpace> cloudSpaces = client.getSpaces();
		final List<CloudApplication> cloudApplications = client.getApplications();
		Map<String, String> spaceOrgMap = new HashMap<>();

		for (CloudSpace cloudSpace : cloudSpaces) {
			spaceOrgMap.put(cloudSpace.getMeta().getGuid().toString(), cloudSpace.getOrganization().getName());
		}
		List<ChargeBackUsageResponse> chargeBackUsageResponseList = new ArrayList<>();
		for (CloudApplication cloudApplication : cloudApplications) {
			for (InstanceStats instanceStats : client.getApplicationStats(cloudApplication.getName()).getRecords()) {
				final ChargeBackUsageResponse chargeBackUsageResponse = new ChargeBackUsageResponse();
				chargeBackUsageResponse.setAppname(cloudApplication.getName());
				chargeBackUsageResponse.setCpu(instanceStats.getUsage().getCpu());
				chargeBackUsageResponse.setDisk(instanceStats.getUsage().getDisk());
				chargeBackUsageResponse.setInstanceIndex(instanceStats.getId());
				chargeBackUsageResponse.setMemory(instanceStats.getUsage().getMem());
				chargeBackUsageResponse.setTime(instanceStats.getUsage().getTime());
				chargeBackUsageResponse.setSpaceName(cloudApplication.getSpace().getName());
				chargeBackUsageResponse
						.setOrgName(spaceOrgMap.get(cloudApplication.getSpace().getMeta().getGuid().toString()));
				chargeBackUsageResponseList.add(chargeBackUsageResponse);
			}
		}
		return chargeBackUsageResponseList;
	}

	@RequestMapping(value = "/getSpaceList/{orgName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getApplicationSpaceByOrg(@PathVariable String orgName) throws UnsupportedEncodingException {
		log.info(String.format("getSpaceList, %s", orgName));

		CloudFoundryClient client = loginCloudFoundry();
		client.login();
		String decodedOrgName = URLDecoder.decode(orgName, "UTF-8");
		return client.getSpaces().stream()
				.filter(cloudspace -> cloudspace.getOrganization().getName().equals(decodedOrgName))
				.map(cloudspace -> cloudspace.getName()).collect(Collectors.toList());
	}

	@RequestMapping(value = "/getOrgList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getOrgList() {
		log.info(String.format("getOrgList"));
		CloudFoundryClient client = loginCloudFoundry();
		client.login();
		return client.getOrganizations().stream().map(org -> org.getName()).collect(Collectors.toList());
	}

	private URL getTargetURL(String target) {
		try {
			return URI.create(target).toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The target URL is not valid: " + e.getMessage());
		}
	}

	private CloudFoundryClient loginCloudFoundry() {
		CloudCredentials credentials = new CloudCredentials("amit.bansal@capgemini.com", "Capgemini2016");
		// CloudFoundryClient client = new CloudFoundryClient(credentials,
		// getTargetURL("https://api.cglean.com"));
		CloudFoundryClient client = new CloudFoundryClient(credentials, getTargetURL("http://api.cglean.com"), null,
				(HttpProxyConfiguration) null, true);
		return client;

	}

	@RequestMapping(value = "/getapps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ChargeBackUsageResponse> getapps() {
		log.info(String.format("getapps"));

		CloudFoundryClient client = loginCloudFoundry();
		final List<CloudSpace> cloudSpaces = client.getSpaces();
		final List<CloudApplication> cloudApplications = client.getApplications();
		Map<String, String> spaceOrgMap = new HashMap<>();

		for (CloudSpace cloudSpace : cloudSpaces) {
			spaceOrgMap.put(cloudSpace.getMeta().getGuid().toString(), cloudSpace.getOrganization().getName());
		}
		List<ChargeBackUsageResponse> chargeBackUsageResponseList = new ArrayList<>();
		for (CloudApplication cloudApplication : cloudApplications) {
			for (InstanceStats instanceStats : client.getApplicationStats(cloudApplication.getName()).getRecords()) {
				final ChargeBackUsageResponse chargeBackUsageResponse = new ChargeBackUsageResponse();
				chargeBackUsageResponse.setAppname(cloudApplication.getName());
				chargeBackUsageResponse.setCpu(instanceStats.getUsage().getCpu());
				chargeBackUsageResponse.setDisk(instanceStats.getUsage().getDisk());
				chargeBackUsageResponse.setInstanceIndex(instanceStats.getId());
				chargeBackUsageResponse.setMemory(instanceStats.getUsage().getMem());
				chargeBackUsageResponse.setTime(instanceStats.getUsage().getTime());
				chargeBackUsageResponse.setSpaceName(cloudApplication.getSpace().getName());
				chargeBackUsageResponse
						.setOrgName(spaceOrgMap.get(cloudApplication.getSpace().getMeta().getGuid().toString()));
				chargeBackUsageResponseList.add(chargeBackUsageResponse);
			}
		}
		return chargeBackUsageResponseList;

	}

	/**
	 * Store the Data into database
	 * 
	 * @param usageList
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void storeHistoricalRecords(@RequestBody List<Usage> usageList) {
		log.info(String.format("storeHistoricalRecords"));
		for (Usage usage : usageList) {
			chargebackService.persistUsageData(usage);
		}
	}

	@RequestMapping(value = "/getHistorical/{fromDate}/{toDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, List<Usage>> getUsageDataBetweenDates(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
		log.info(String.format("getHistorical %s, %s", fromDate, toDate));
		return chargebackService.getUsageDataBetweenDates(fromDate, toDate);
	}

	@RequestMapping(value = "/submit/summary", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void storeUsageSummary(@RequestBody List<UsageSummary> usageSummaryList) {
		log.info(String.format("storeUsageSummary"));
		for (UsageSummary usageSummary : usageSummaryList) {
			chargebackService.persistUsageSummaryData(usageSummary);
		}
	}

	@RequestMapping(value = "/getQuota/{resourceType}/{orgName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Double getQuotaType(@PathVariable final String resourceType, @PathVariable final String orgName) {
		log.info(String.format("getQuota, %s, %s", resourceType, orgName));
		CloudFoundryClient client = loginCloudFoundry();

		final List<CloudApplication> cloudApplications = client.getApplications();
		final List<CloudSpace> cloudSpaces = client.getSpaces();
		ChargeBackAggregrateVO chargeBackAggregrateVO;
		final List<ChargeBackAggregrateVO> chargeBackAggregrateVOList = new ArrayList<>();
		for (final CloudApplication application : cloudApplications) {
			chargeBackAggregrateVO = new ChargeBackAggregrateVO();
			chargeBackAggregrateVO.setApplicationStats(client.getApplicationStats(application.getName()));
			chargeBackAggregrateVO.setCloudApplication(application);
			chargeBackAggregrateVO.setSpaces(cloudSpaces);
			chargeBackAggregrateVOList.add(chargeBackAggregrateVO);
		}

		final List<ChargeBackUsageResponse> chargeBackUsageResponseList = chargebackService
				.getChargeBackUsage(chargeBackAggregrateVOList);
		if (resourceType.equals("MEM")) {
			double mem = 0.0;
			for (final ChargeBackUsageResponse chargeBackUsageResponse : chargeBackUsageResponseList) {
				if (chargeBackUsageResponse.getOrgName().equals(orgName)) {
					mem += Double.valueOf(
							client.getApplication(chargeBackUsageResponse.getAppname()).getMemory() * 1024 * 1024);
				}
			}
			return mem;
		} else if (resourceType.equals("CPU")) {
			double cpu = 0.0;

			for (final ChargeBackUsageResponse chargeBackUsageResponse : chargeBackUsageResponseList) {

				if (chargeBackUsageResponse.getOrgName().equals(orgName)) {

					int size = client.getApplicationStats(chargeBackUsageResponse.getAppname()).getRecords().size();
					cpu += ((client.getApplicationStats(chargeBackUsageResponse.getAppname()).getRecords().get(0)
							.getUsage().getCpu()) / size);
				}
			}

			return cpu;
		} else if (resourceType.equals("DISK")) {
			double disk = 0.0;
			for (final ChargeBackUsageResponse chargeBackUsageResponse : chargeBackUsageResponseList) {
				if (chargeBackUsageResponse.getOrgName().equals(orgName)) {
					disk += Double.valueOf(
							client.getApplication(chargeBackUsageResponse.getAppname()).getDiskQuota() * 1024 * 1024);
				}
			}
			return disk;
		}
		return null;
	}

	@RequestMapping(value = "/getTotalQuota/{resourceType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Double getTotalQuota(@PathVariable final String resourceType) {
		log.info(String.format("getTotalQuota, %s, %s", resourceType));
		CloudFoundryClient client = loginCloudFoundry();
		if (resourceType.equals("MEM")) {
			return Double.valueOf(client.getQuotaByName("default", true).getMemoryLimit() * 1024 * 1024);
		} else if (resourceType.equals("CPU")) {
			return 1.0;
		} else if (resourceType.equals("DISK")) {

			return Double.valueOf(client.getApplications().stream().mapToLong(cloudapp -> cloudapp.getDiskQuota()).sum()
					* 1024 * 1024);
		}

		return null;
	}
}
