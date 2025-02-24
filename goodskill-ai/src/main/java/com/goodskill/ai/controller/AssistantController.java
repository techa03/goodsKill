package com.goodskill.ai.controller;

import com.goodskill.ai.service.UserSupportAssistant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @author techa03
 */
@RequestMapping("/api/assistant")
@RestController
public class AssistantController {

	private final UserSupportAssistant agent;

	public AssistantController(UserSupportAssistant agent) {
		this.agent = agent;
	}

	@RequestMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> chat(@RequestParam String chatId, @RequestParam String userMessage) {
		return agent.chat(chatId, userMessage);
	}

}
