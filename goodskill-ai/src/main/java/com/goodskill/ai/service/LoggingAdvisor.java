package com.goodskill.ai.service;

import org.springframework.ai.chat.client.RequestResponseAdvisor;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;

import java.util.Map;

/**
 * @author techa03
 */
public class LoggingAdvisor implements RequestResponseAdvisor {

	@Override
	public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
		System.out.println("Request: " + request);
		return request;
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
