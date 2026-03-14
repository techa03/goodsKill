/*
 * Copyright 2024-2026 the original author or authors.
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

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.extension.tools.filesystem.ReadFileTool;
import com.alibaba.cloud.ai.graph.agent.hook.shelltool.ShellToolAgentHook;
import com.alibaba.cloud.ai.graph.agent.tools.ShellTool;
import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import com.alibaba.cloud.ai.graph.serializer.StateSerializer;
import com.alibaba.cloud.ai.graph.serializer.std.SpringAIStateSerializer;
import com.goodskill.ai.tool.PythonTool;
import com.goodskill.ai.tool.SeckillTools;
import com.goodskill.ai.tool.TaskTimeInfoTool;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;


@Configuration
public class ChatbotAgent {

	private static final String INSTRUCTION = """
				您是用户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
				您正在通过在线聊天系统与用户互动。
				您能够支持已有秒杀商品详情查询、开启秒杀活动、重置秒杀活动等操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
			   在提供有关秒杀活动查询、开启秒杀活动、重置秒杀活动等操作之前，您必须始终从用户处获取以下信息：秒杀活动id、秒杀商品数量、请求次数。
			   如果需要提前结束秒杀活动，您必须在继续之前征得用户同意。
			   使用提供的功能获取秒杀活动以及商品详细信息、开启秒杀活动和重置秒杀。
			   用户开始秒杀活动后，等待10秒，然后调用 获取任务耗时统计信息 函数，最后将函数结果返回给用户。
			   如果需要，您可以调用相应函数辅助完成。
			   请讲中文。
			""";

	@Bean
	public ReactAgent chatbotReactAgent(ChatModel chatModel,
			ToolCallback executeShellCommand,
			ToolCallback executePythonCode,
			ToolCallback viewTextFile,
			ToolCallback executeSeckillTool,
			ToolCallback getTaskTimeInfoTool,
			RedisSaver checkpointSaver) {
		return ReactAgent.builder()
				.name("SAA")
				.model(chatModel)
				.instruction(INSTRUCTION)
				.enableLogging(true)
				// Redis 持久化对话 checkpoint
				.saver(checkpointSaver)
				// Must set ShellToolAgentHook to manage shell session lifecycle for executeShellCommand
				.hooks(ShellToolAgentHook.builder().shellToolName(executeShellCommand.getToolDefinition().name()).build())
				.tools(
						executeShellCommand,
						executePythonCode,
						viewTextFile,
						executeSeckillTool,
						// 获取任务耗时统计信息工具
						getTaskTimeInfoTool
				)
				.build();
	}

	@Bean
	public StateSerializer stateSerializer() {
		// 使用 SpringAIStateSerializer 统一序列化对话状态
		return new SpringAIStateSerializer();
	}

	@Bean
	public RedisSaver redisSaver(RedissonClient redissonClient, StateSerializer stateSerializer) {
		// RedisSaver 负责 checkpoint 的存取
		return RedisSaver.builder()
				.redisson(redissonClient)
				.stateSerializer(stateSerializer)
				.build();
	}

	// Tool: execute_shell_command
	@Bean
	public ToolCallback executeShellCommand() {
		// Use ShellTool with a temporary workspace directory
		String workspaceRoot = System.getProperty("java.io.tmpdir") + File.separator + "agent-workspace";
		return ShellTool.builder(workspaceRoot)
				.withName("execute_shell_command")
				.withDescription("Execute a shell command inside a persistent session. Before running a command, " +
						"confirm the working directory is correct (e.g., inspect with `ls` or `pwd`) and ensure " +
						"any parent directories exist. Prefer absolute paths and quote paths containing spaces, " +
						"such as `cd \"/path/with spaces\"`. Chain multiple commands with `&&` or `;` instead of " +
						"embedding newlines. Avoid unnecessary `cd` usage unless explicitly required so the " +
						"session remains stable. Outputs may be truncated when they become very large, and long " +
						"running commands will be terminated once their configured timeout elapses.")
				.build();
	}

	// Tool: execute_python_code
	@Bean
	public ToolCallback executePythonCode() {
		return FunctionToolCallback.builder("execute_python_code", new PythonTool())
				.description(PythonTool.DESCRIPTION)
				.inputType(PythonTool.PythonRequest.class)
				.build();
	}

	// Tool: view_text_file
	@Bean
	public ToolCallback viewTextFile() {
		// Create a custom wrapper to match the original tool name
		ReadFileTool readFileTool = new ReadFileTool();
		return FunctionToolCallback.builder("view_text_file", readFileTool)
				.description("View the contents of a text file. The file_path parameter must be an absolute path. " +
						"You can specify offset and limit to read specific portions of the file. " +
						"By default, reads up to 500 lines starting from the beginning of the file.")
				.inputType(ReadFileTool.ReadFileRequest.class)
				.build();
	}

	@Bean
	public ToolCallback executeSeckillTool(SeckillTools seckillTools) {
		return FunctionToolCallback.builder("execute_seckill", seckillTools)
				.description("执行模拟秒杀")
				.inputType(SeckillTools.StartSeckillRequest.class)
				.build();
	}

	@Bean
	public ToolCallback getTaskTimeInfoTool(TaskTimeInfoTool taskTimeInfoTool) {
		return FunctionToolCallback.builder("get_task_time_info", taskTimeInfoTool)
				.description("获取任务耗时统计信息，你需要从execute_seckill返回的结果作为taskId，使用taskId任务id作为请求入参调用获取任务耗时统计信息，其他参数可以使用null")
				.inputType(TaskTimeInfoTool.GetTaskTimeInfoRequest.class)
				.build();
	}

}
