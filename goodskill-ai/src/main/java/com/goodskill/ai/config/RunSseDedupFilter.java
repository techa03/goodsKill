package com.goodskill.ai.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 对 /run_sse 的 SSE 响应做去重兜底（MVC）
 *
 * @author bot
 *
 * 临时修复 1.1.2.0 SAA sse消息重复问题
 */
@Component
public class RunSseDedupFilter extends OncePerRequestFilter {

	private static final String TARGET_PATH = "/run_sse";
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 只处理 studio 的 run_sse
		if (!TARGET_PATH.equals(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}
		StreamingResponseWrapper wrapper = new StreamingResponseWrapper(response);
		// 固定 UTF-8，避免中文乱码
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		// 走下游并在写出时过滤重复事件
		filterChain.doFilter(request, wrapper);
		// 确保最后剩余数据写出
		wrapper.flushRemaining();
	}

	/**
	 * 只保留最终消息，去掉 chunk 导致的重复
	 */
	private EventDecision decideEvent(String event, boolean sawChunk) {
		// 解析 data 行并决定是否保留
		String data = extractData(event);
		if (data == null || data.isEmpty()) {
			return EventDecision.PASS;
		}
		if ("{}".equals(data)) {
			return EventDecision.DROP;
		}
		ObjectNode node = parseObject(data);
		if (node == null) {
			return EventDecision.PASS;
		}
		String finishReason = readFinishReason(node);
		// 发现 chunk 时直接透传，标记已分片
		String chunk = readChunk(node);
		if (chunk != null && !chunk.isEmpty()) {
			// 结束事件且已经有分片时，丢弃重复的最终消息
			if (sawChunk && "STOP".equalsIgnoreCase(finishReason)) {
				return EventDecision.DROP;
			}
			return EventDecision.CHUNK;
		}
		// 发现最终消息时：若已分片则丢弃，避免重复
		String messageContent = readMessageContent(node);
		if (messageContent != null && !messageContent.isEmpty()) {
			return sawChunk ? EventDecision.DROP : EventDecision.PASS;
		}
		return EventDecision.PASS;
	}

	private String extractData(String event) {
		String[] lines = event.split("\\r?\\n");
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			if (line.startsWith("data:")) {
				sb.append(line.substring(5));
			}
		}
		return sb.length() == 0 ? null : sb.toString().trim();
	}

	private ObjectNode parseObject(String json) {
		try {
			JsonNode node = mapper.readTree(json);
			if (node instanceof ObjectNode) {
				return (ObjectNode) node;
			}
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	private String readMessageContent(ObjectNode node) {
		JsonNode message = node.get("message");
		if (message == null || message.isNull()) {
			return null;
		}
		JsonNode content = message.get("content");
		return content == null || content.isNull() ? null : content.asText();
	}

	private String readFinishReason(ObjectNode node) {
		JsonNode message = node.get("message");
		if (message == null || message.isNull()) {
			return null;
		}
		JsonNode metadata = message.get("metadata");
		if (metadata == null || metadata.isNull()) {
			return null;
		}
		JsonNode finishReason = metadata.get("finishReason");
		return finishReason == null || finishReason.isNull() ? null : finishReason.asText();
	}

	private String readChunk(ObjectNode node) {
		JsonNode chunk = node.get("chunk");
		return chunk == null || chunk.isNull() ? null : chunk.asText();
	}

	/**
	 * 流式过滤响应内容，避免阻塞 SSE
	 */
	private class StreamingResponseWrapper extends HttpServletResponseWrapper {
		private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		private PrintWriter writer;
		private ServletOutputStream outputStream;
		private boolean sawChunk = false;

		StreamingResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public ServletOutputStream getOutputStream() {
			if (outputStream == null) {
				outputStream = new ServletOutputStream() {
					@Override
					public boolean isReady() {
						return true;
					}

					@Override
					public void setWriteListener(WriteListener writeListener) {
						// no-op
					}

					@Override
					public void write(int b) {
						buffer.write(b);
						// 处理完整事件
						flushEventsIfReady();
					}
				};
			}
			return outputStream;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			if (writer == null) {
				Charset charset = Charset.forName(getCharacterEncoding());
				writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), charset), true);
			}
			return writer;
		}

		void flushRemaining() throws IOException {
			if (writer != null) {
				writer.flush();
			}
			flushEventsIfReady();
		}

		private void flushEventsIfReady() {
			// 每个 SSE 事件以空行结尾
			while (true) {
				byte[] bytes = buffer.toByteArray();
				Delimiter delimiter = findDelimiter(bytes);
				if (delimiter == null) {
					return;
				}
				byte[] eventBytes = new byte[delimiter.index + delimiter.length];
				System.arraycopy(bytes, 0, eventBytes, 0, eventBytes.length);
				byte[] remaining = new byte[bytes.length - eventBytes.length];
				System.arraycopy(bytes, eventBytes.length, remaining, 0, remaining.length);
				buffer.reset();
				buffer.writeBytes(remaining);
				// 解析事件文本用于判断类型
				String event = new String(eventBytes, StandardCharsets.UTF_8);
				EventDecision decision = decideEvent(event, sawChunk);
				if (decision == EventDecision.CHUNK) {
					sawChunk = true;
				}
				if (decision == EventDecision.DROP) {
					continue;
				}
				writeDirectBytes(eventBytes);
			}
		}

		private void writeDirectBytes(byte[] data) {
			try {
				getResponse().getOutputStream().write(data);
				getResponse().flushBuffer();
			} catch (IOException ex) {
				// ignore
			}
		}
	}

	private enum EventDecision {
		PASS,
		CHUNK,
		DROP
	}

	private static class Delimiter {
		private final int index;
		private final int length;

		private Delimiter(int index, int length) {
			this.index = index;
			this.length = length;
		}
	}

	private Delimiter findDelimiter(byte[] bytes) {
		int idxLf = indexOf(bytes, new byte[]{'\n', '\n'});
		int idxCrLf = indexOf(bytes, new byte[]{'\r', '\n', '\r', '\n'});
		if (idxLf < 0 && idxCrLf < 0) {
			return null;
		}
		if (idxLf >= 0 && (idxCrLf < 0 || idxLf <= idxCrLf)) {
			return new Delimiter(idxLf, 2);
		}
		return new Delimiter(idxCrLf, 4);
	}

	private int indexOf(byte[] bytes, byte[] pattern) {
		if (bytes.length < pattern.length) {
			return -1;
		}
		for (int i = 0; i <= bytes.length - pattern.length; i++) {
			boolean match = true;
			for (int j = 0; j < pattern.length; j++) {
				if (bytes[i + j] != pattern[j]) {
					match = false;
					break;
				}
			}
			if (match) {
				return i;
			}
		}
		return -1;
	}
}
