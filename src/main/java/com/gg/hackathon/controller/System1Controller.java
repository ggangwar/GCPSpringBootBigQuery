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

import com.gg.hackathon.service.System1Service;

@RestController
@RequestMapping("v1")
public class System1Controller {
	Logger LOG = LoggerFactory.getLogger(System1Controller.class);

	@Autowired
	private System1Service system1Service;

	@GetMapping(path = "/system1/{tradeId}")
	public String fetchCounterpartyDetails(@PathVariable("tradeId") String tradeId) throws IOException {
		LOG.info("Received request to get trade details for [{}]", tradeId);
		InputStream inputStream = system1Service.get(tradeId);
		Path tempFile = Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");
		Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
		String result = new String(Files.readAllBytes(tempFile));
		inputStream.close();
		return result;
	}
}
