package com.example.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.chargeback.ChargeBackMetricsFetchApplication;
import com.chargeback.entity.Usage;
import com.chargeback.repository.UsageRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ChargeBackMetricsFetchApplication.class)
public class UsageRepositoryTests {

	@Autowired
	private UsageRepository repository;

	@Test
	public void saveAndFind() {
		Usage usage = new Usage();
		usage.setOrgName(this.getClass().getName());
		usage = repository.save(usage);
		long id = usage.getUsageId();
		assertThat(id, not(equalTo(0l)));
		repository.delete(usage);
		usage = repository.findOne(id);
		assertThat(usage, nullValue());
	}
}
