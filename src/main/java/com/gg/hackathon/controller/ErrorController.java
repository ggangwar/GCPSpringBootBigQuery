package com.gg.hackathon.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.hackathon.service.ErrorHandlerService;

@RestController
@RequestMapping("v1")
public class ErrorController {
	Logger LOG = LoggerFactory.getLogger(ErrorController.class);

	@Autowired
	private ErrorHandlerService errorHandlerService;

	@PostMapping(path = "/errorDetails", consumes = "application/json", produces = "application/json")
	public String saveErrorDetails(@RequestBody Map<String, String> errorDetails) {
		LOG.info("Received error details [{}]", errorDetails);
		return errorHandlerService.save(errorDetails);
	}

	@GetMapping(path = "/errorDetails/{errorCode}", produces = "application/json")
	public String fetchErrorDetails(@PathVariable("errorCode") String errorCode) {
		LOG.info("Received request to get error details for [{}]", errorCode);
		String result = errorHandlerService.get(errorCode);
		LOG.info("Does error tracker exists for {}:{}", errorCode, result);
		return result;
	}
}
