package com.gg.hackathon.util;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

@Component
public class BigQueryProvider {

	Logger LOG = LoggerFactory.getLogger(BigQueryProvider.class);
	private BigQuery bigquery = null;

	@PostConstruct
	public void init() {
		try {
			this.bigquery = BigQueryOptions.newBuilder().setProjectId("dogwood-cinema-211414")
					.setCredentials(ServiceAccountCredentials.fromStream(getClass().getResourceAsStream("/key.json")))
					.build().getService();
		} catch (IOException e) {
			LOG.error("Exception occurred while instantiating BigQuery", e);
		}
	}

	public BigQuery getBigquery() {
		return bigquery;
	}

}
