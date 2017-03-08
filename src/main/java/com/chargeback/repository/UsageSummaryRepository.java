package com.chargeback.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chargeback.entity.UsageSummary;

public interface UsageSummaryRepository extends JpaRepository<UsageSummary, Serializable>{

}
