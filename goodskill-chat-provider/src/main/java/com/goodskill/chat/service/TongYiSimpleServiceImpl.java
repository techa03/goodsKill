package com.goodskill.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
public class TongYiSimpleServiceImpl extends AbstractTongYiServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(TongYiSimpleServiceImpl.class);

    private final ChatModel chatModel;

    private final StreamingChatModel streamingChatModel;

    @Autowired
    public TongYiSimpleServiceImpl(ChatModel chatModel, StreamingChatModel streamingChatModel) {

        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
    }

    @Override
    public String completion(String message) {

        Prompt prompt = new Prompt(new UserMessage(message));

        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    @Override
    public Map<String, String> streamCompletion(String message) {

        StringBuilder fullContent = new StringBuilder();

        streamingChatModel.stream(new Prompt(message))
                .flatMap(chatResponse -> Flux.fromIterable(chatResponse.getResults()))
                .map(content -> content.getOutput().getContent())
                .doOnNext(fullContent::append)
                .last()
                .map(lastContent -> Map.of(message, fullContent.toString()))
                .block();

        logger.info(fullContent.toString());

        return Map.of(message, fullContent.toString());
    }

}
