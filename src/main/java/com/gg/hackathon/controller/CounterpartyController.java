package com.gg.hackathon.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.hackathon.service.CounterpartyService;

@RestController
@RequestMapping("v1")
public class CounterpartyController {
	Logger LOG = LoggerFactory.getLogger(CounterpartyController.class);

	@Autowired
	private CounterpartyService counterpartyService;

	@GetMapping(path = "/counterparty/{counterpartyId}")
	public String fetchCounterpartyDetails(@PathVariable("counterpartyId") String counterpartyId) throws IOException {
		LOG.info("Received request to get counterparty details for [{}]", counterpartyId);
		InputStream inputStream = counterpartyService.get(counterpartyId);
		Path tempFile = Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");
		Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
		String result = new String(Files.readAllBytes(tempFile));
		inputStream.close();
		return result;
	}
}
