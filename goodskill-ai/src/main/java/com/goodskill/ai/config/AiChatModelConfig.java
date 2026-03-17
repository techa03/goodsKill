package com.goodskill.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * ChatModel 选择配置
 */
@Configuration
public class AiChatModelConfig {

	/**
	 * 根据配置选择 ChatModel
	 */
	@Bean
	@Primary
	public ChatModel primaryChatModel(
			@Value("${goodskill.ai.provider:dashscope}") String provider,
			ObjectProvider<List<ChatModel>> modelsProvider) {
		// 获取所有可用的 ChatModel 实例
		List<ChatModel> models = modelsProvider.getIfAvailable();
		if (models == null || models.isEmpty()) {
			throw new IllegalStateException("未发现可用的ChatModel实现");
		}
		// 归一化 provider
		String normalized = normalizeProvider(provider);
		// 按 provider 选择模型
		ChatModel selected = selectByProvider(models, normalized);
		if (selected == null) {
			throw new IllegalStateException("未找到匹配的ChatModel: " + normalized);
		}
		return selected;
	}

	/**
	 * provider 归一化
	 */
	private String normalizeProvider(String provider) {
		if (provider == null || provider.trim().isEmpty()) {
			return "dashscope";
		}
		return provider.trim().toLowerCase();
	}

	/**
	 * 根据 provider 选择 ChatModel
	 */
	private ChatModel selectByProvider(List<ChatModel> models, String provider) {
		// 根据类名判断来源，避免硬依赖具体实现类
		for (ChatModel model : models) {
			String className = model.getClass().getName().toLowerCase();
			if ("ollama".equals(provider) && className.contains("ollama")) {
				return model;
			}
			if ("dashscope".equals(provider) && className.contains("dashscope")) {
				return model;
			}
		}
		// 未匹配则返回 null
		return null;
	}
}
