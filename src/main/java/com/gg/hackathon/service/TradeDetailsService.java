package com.gg.hackathon.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gg.hackathon.util.BigQueryProvider;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;

@Service
public class TradeDetailsService {

	private Logger LOG = LoggerFactory.getLogger(TradeDetailsService.class);
	private static Random random = new Random();
	private static final String[] ERRORS = { "Invalid LEI  : Trade Part 2 -ID",
			"Incorrect Trade Date Time Format : TradeDateTime conversion mismatch",
			"Missing LEI : LEI is missing in Trade Part 1 -ID", "Blank UTI : UTI ID, UTI ID Prefix is blank",
			"Incorrect Trade Direction : Value is incorrect for Trade Party1 Counterparty Side",
			"Missing Counterparty ID : Counterparty ID is blank", "Blank MIC : Execution Venue MIC CODE is blank",
			"LEI Mismatch : Trade Party1 and Party2", "Trade Date and Time in Incorrect format",
			"Strike price in negative", "Security ISIN Missing" };

	@Autowired
	private BigQueryProvider bigQueryProvider;

	public void save(Map<String, String> tradeDetails) {
		Timestamp timestamp = null;
		try {
			try {
				LocalDateTime localDateTime = LocalDateTime.parse(tradeDetails.get("Trade Date Time").substring(0,
						tradeDetails.get("Trade Date Time").lastIndexOf(':')));
				timestamp = Timestamp.valueOf(localDateTime);
			} catch (Exception e) {
				LOG.error("Exception occurred while parsing date: {}", tradeDetails.get("Trade Date Time"), e);
				timestamp = Timestamp.valueOf(LocalDateTime.now());
			}
			String counterpartyId = tradeDetails.get("Counterparty ID");
			if(null == counterpartyId) {
				counterpartyId = "CPTY11";
			}
			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder("INSERT INTO\n"
					+ "  hackathon.TradeDetails (TradeId, Action, TradeDateTime, LifecycleEventType, MessageID, ProductIdnetification,"
					+ "TradeParty1ID, TradeParty2ID, UTIID, UTIIDprefix, GTRValidationStatus, TradeParty1CounterartySide, ExecutionVenueMICCODE, CounterpartyID, Error, TradeDetails)"
					+ "VALUES (\"" + tradeDetails.get("TradeId") + "\",\"" + tradeDetails.get("Action") + "\",\""
					+ timestamp + "\",\"" + tradeDetails.get("Lifecycle Event Type") + "\",\""
					+ tradeDetails.get("Message ID") + "\",\"" + tradeDetails.get("Product Idnetification") + "\",\""
					+ tradeDetails.get("Trade Party 1 - ID") + "\",\"" + tradeDetails.get("Trade Party 2 - ID")
					+ "\",\"" + tradeDetails.get("UTI ID") + "\",\"" + tradeDetails.get("UTI ID prefix") + "\",\""
					+ tradeDetails.get("GTR Validation Status") + "\",\""
					+ tradeDetails.get("Trade Party 1 Counterarty Side") + "\",\""
					+ tradeDetails.get("Execution Venue MIC CODE") + "\",\"" + counterpartyId 
					+ "\",\"" + getError() + "\",\"" + tradeDetails.toString() + "\")").setUseLegacySql(false).build();

			// Create a job ID so that we can safely retry.
			JobId jobId = JobId.of(UUID.randomUUID().toString());
			Job queryJob = bigQueryProvider.getBigquery()
					.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

			// Wait for the query to complete.

			queryJob = queryJob.waitFor();

			// Check for errors
			if (queryJob == null) {
				throw new RuntimeException("Job no longer exists");
			} else if (queryJob.getStatus().getError() != null) {
				throw new RuntimeException(queryJob.getStatus().getError().toString());
			} else {
				LOG.info("Trade details are inserted successfully");
			}

		} catch (JobException | InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private static String getError() {
		int randomNumber = random.nextInt(ERRORS.length);
		return ERRORS[randomNumber];
	}
}
