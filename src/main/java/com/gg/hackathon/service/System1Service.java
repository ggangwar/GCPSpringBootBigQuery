package com.gg.hackathon.service;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class System1Service {
	Logger LOG = LoggerFactory.getLogger(System1Service.class);

	public void save(MultipartFile counterparty) {
		LOG.info("Received counterparty json for:{}", counterparty.getOriginalFilename());
	}

	public InputStream get(String tradeId) {
		return getClass().getResourceAsStream("/System1/" + tradeId+".json");
	}

}
