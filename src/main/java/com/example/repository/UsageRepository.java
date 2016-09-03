package com.example.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.entity.Usage;

public interface UsageRepository extends CrudRepository<Usage, Serializable>{

	@Query("select usage from Usage usage " +
	         "where usage.createdTime between :fromDate and :toDate and usage.orgName= :orgName and usage.spaceName= :space and usage.appname= :appname and usage.instanceIndex= :index")
	public List<Usage> findByDateAndNameAndApplication(@Param("fromDate")final Date fromDate, @Param("toDate")final Date toDate,
			@Param("orgName")final String orgName, @Param("space")final String spaceName , @Param("appname")final String appname, @Param("index")final int Index);
	
	@Query("SELECT DISTINCT usage.orgName FROM Usage usage where usage.createdTime between :fromDate and :toDate")
	List<String> findDistinctOrgName(@Param("fromDate")final Date fromDate, @Param("toDate")Date toDate);
	
	@Query("SELECT DISTINCT usage.spaceName FROM Usage usage where usage.createdTime between :fromDate and :toDate and usage.orgName= :orgName")
	List<String> findDistinctSpaceName(@Param("fromDate")final Date fromDate, @Param("toDate")final Date toDate, @Param("orgName")final String orgName);
	
	@Query("SELECT DISTINCT usage.appname FROM Usage usage where usage.createdTime between :fromDate and :toDate and usage.orgName= :orgName and usage.spaceName= :space")
	List<String> findDistinctApps(@Param("fromDate")final Date fromDate, @Param("toDate")final Date toDate, @Param("orgName")final String orgName, @Param("space")final String space );
	
	@Query("SELECT DISTINCT usage.instanceIndex FROM Usage usage where usage.createdTime between :fromDate and :toDate and usage.orgName= :orgName and usage.spaceName= :space and usage.appname= :appname")
	List<Integer> findIndexesforApp(@Param("fromDate")final Date fromDate, @Param("toDate")final Date toDate, @Param("orgName")final String orgName, @Param("space")final String space , @Param("appname")final String appname);
}
