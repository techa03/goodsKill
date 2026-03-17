package com.goodskill.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @author techa03
 */
@RequestMapping("/assistant")
@RestController
public class AssistantController {

	private final ChatClient chatClient;

	public AssistantController(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@RequestMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> chat(@RequestParam String chatId, @RequestParam String userMessage) {
		return chatClient.prompt().user(userMessage).stream().content();
	}

}
