package com.gg.hackathon.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.hackathon.service.TradeDetailsService;

@RequestMapping("v1")
@RestController
public class TradeDetailsController {

	Logger LOG = LoggerFactory.getLogger(TradeDetailsController.class);
	
	@Autowired
	private TradeDetailsService tradeDetailsService;
	
	@PostMapping(path = "/tradeDetails", consumes = "application/json", produces = "application/json")
	public void saveTradeDetails(@RequestBody Map<String, String> tradeDetails) {
		LOG.info("Received trade details [{}]", tradeDetails);
		tradeDetailsService.save(tradeDetails);
	}
}
