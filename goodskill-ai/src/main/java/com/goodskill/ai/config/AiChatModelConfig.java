package com.goodskill.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * ChatModel 和 EmbeddingModel 选择配置
 * 默认 provider 仅在配置文件未配置时生效
 */
@Configuration
public class AiChatModelConfig {

	/**
	 * 根据配置选择 ChatModel
	 * @param provider AI 提供商（可选值：dashscope, ollama；默认：dashscope）
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
	 * 根据配置选择 EmbeddingModel
	 * @param provider AI 提供商（可选值：dashscope, ollama；默认：dashscope）
	 */
	@Bean
	@Primary
	public EmbeddingModel primaryEmbeddingModel(
			@Value("${goodskill.ai.provider:dashscope}") String provider,
			ObjectProvider<List<EmbeddingModel>> modelsProvider) {
		// 获取所有可用的 EmbeddingModel 实例
		List<EmbeddingModel> models = modelsProvider.getIfAvailable();
		if (models == null || models.isEmpty()) {
			throw new IllegalStateException("未发现可用的EmbeddingModel实现");
		}
		// 归一化 provider
		String normalized = normalizeProvider(provider);
		// 按 provider 选择模型
		EmbeddingModel selected = selectEmbeddingModelByProvider(models, normalized);
		if (selected == null) {
			throw new IllegalStateException("未找到匹配的EmbeddingModel: " + normalized);
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

	/**
	 * 根据 provider 选择 EmbeddingModel
	 */
	private EmbeddingModel selectEmbeddingModelByProvider(List<EmbeddingModel> models, String provider) {
		// 根据类名判断来源，避免硬依赖具体实现类
		for (EmbeddingModel model : models) {
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
