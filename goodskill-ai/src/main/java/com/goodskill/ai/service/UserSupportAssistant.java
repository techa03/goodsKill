/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goodskill.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * * @author Christian Tzolov
 */
@Service
public class UserSupportAssistant {

    private final ChatClient chatClient;

    public UserSupportAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory) {

        // @formatter:off
		this.chatClient = modelBuilder
				.defaultSystem("""
						您是用户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
						您正在通过在线聊天系统与用户互动。
						您能够支持已有秒杀商品详情查询、开启秒杀活动、重置秒杀活动等操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
					   在提供有关秒杀活动查询、开启秒杀活动、重置秒杀活动等操作之前，您必须始终从用户处获取以下信息：秒杀活动id、秒杀商品数量、请求次数。
					   如果需要提前结束秒杀活动，您必须在继续之前征得用户同意。
					   使用提供的功能获取秒杀活动以及商品详细信息、开启秒杀活动和重置秒杀。
					   用户开始秒杀活动后，等待10秒，然后调用 获取任务耗时统计信息 函数，最后将函数结果返回给用户。
					   如果需要，您可以调用相应函数辅助完成。
					   请讲中文。
					   今天的日期是 {current_date}.
					""")
				.defaultAdvisors(
						// Chat Memory
						new PromptChatMemoryAdvisor(chatMemory),
						// new VectorStoreChatMemoryAdvisor(vectorStore)),
						// RAG
						new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().topK(4).similarityThresholdAll().build()),
						// new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()
						// 	.withFilterExpression("'documentType' == 'terms-of-service' && region in ['EU', 'US']")),
						new LoggingAdvisor())
				// FUNCTION CALLING
				.defaultFunctions("startSeckill", "getTaskTimeInfo")
				.build();
		// @formatter:on
    }

    public Flux<String> chat(String chatId, String userMessageContent) {
        return this.chatClient.prompt().system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
				.content();
    }

}
