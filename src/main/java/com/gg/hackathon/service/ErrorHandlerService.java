package com.gg.hackathon.service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gg.hackathon.util.BigQueryProvider;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

@Service
public class ErrorHandlerService {

	private static final Random RANDOM = new Random();
	Logger LOG = LoggerFactory.getLogger(ErrorHandlerService.class);

	@Autowired
	private BigQueryProvider bigQueryProvider;

	public String save(Map<String, String> errorDetails) {
		String trackerId = get(errorDetails.get("GTR Error Descriptions"));
		if (!"NOTFOUND".equals(trackerId)) {
			return trackerId;
		} else {
			trackerId = jiraRef();
			try {
				QueryJobConfiguration queryConfig = QueryJobConfiguration
						.newBuilder(
								"INSERT INTO\n" + "  hackathon.ErrorTracker (Description, ActionItem)" + "VALUES (\""
										+ errorDetails.get("GTR Error Descriptions") + "\",\"" + trackerId + "\")")
						.setUseLegacySql(false).build();

				JobId jobId = JobId.of(UUID.randomUUID().toString());
				Job queryJob = bigQueryProvider.getBigquery()
						.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

				queryJob = queryJob.waitFor();

				if (queryJob == null) {
					throw new RuntimeException("Job no longer exists");
				} else if (queryJob.getStatus().getError() != null) {
					throw new RuntimeException(queryJob.getStatus().getError().toString());
				} else {
					LOG.info("Error details are inserted successfully for {}", trackerId);
				}

			} catch (JobException | InterruptedException e) {
				throw new IllegalStateException(e);
			}
			return trackerId;
		}
	}

	private static String jiraRef() {
		return "FXOP-" + RANDOM.nextInt(10000);
	}

	public String get(String errorCode) {
		try {
			QueryJobConfiguration queryConfig = QueryJobConfiguration
					.newBuilder("SELECT * FROM hackathon.ErrorTracker WHERE Description like '%" + errorCode + "%'")
					.setUseLegacySql(false).build();

			JobId jobId = JobId.of(UUID.randomUUID().toString());
			Job queryJob = bigQueryProvider.getBigquery()
					.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

			queryJob = queryJob.waitFor();

			if (queryJob == null) {
				throw new RuntimeException("Job no longer exists");
			} else if (queryJob.getStatus().getError() != null) {
				throw new RuntimeException(queryJob.getStatus().getError().toString());
			}

			TableResult result = queryJob.getQueryResults();
			if (result != null && result.getTotalRows() > 0) {
				for (FieldValueList row : result.iterateAll()) {
					String id = row.get("ActionItem").getStringValue();
					return id;
				}
			}
		} catch (JobException | InterruptedException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		return "NOTFOUND";
	}
}
