package com.goodskill.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.goodskill.core.info.Result;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 统一响应包装过滤器
 * 确保所有 C 端后端接口都使用 Result 类包装返回值
 */
@Component
@Slf4j
public class ResponseWrapperFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();

        // 装饰响应，拦截响应体
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                // 只处理 JSON 响应
                if (isJsonResponse(getDelegate())) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        // 合并数据缓冲区
                        DataBufferFactory dataBufferFactory = getDelegate().bufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);

                        // 解析响应体
                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        try {
                            // 检查是否为空响应
                            if (responseBody == null || responseBody.trim().isEmpty()) {
                                return dataBufferFactory.wrap(content);
                            }
                            
                            // 尝试解析为 JSON
                            Object jsonElement = JSON.parse(responseBody);
                            
                            // 如果已经是 Result 包装（JSON 对象且包含 code、msg、data 字段）
                            if (jsonElement instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) jsonElement;
                                if (jsonObject.containsKey("code") && jsonObject.containsKey("msg") && jsonObject.containsKey("data")) {
                                    // 已经是 Result 包装，直接返回
                                    return dataBufferFactory.wrap(content);
                                } else {
                                    // 不是 Result 包装，包装为 Result
                                    Result<Object> result = Result.ok(jsonObject);
                                    String wrappedResponse = JSON.toJSONString(result);
                                    return dataBufferFactory.wrap(wrappedResponse.getBytes(StandardCharsets.UTF_8));
                                }
                            } else {
                                // JSON 数组或其他类型，直接包装为 Result
                                Result<Object> result = Result.ok(jsonElement);
                                String wrappedResponse = JSON.toJSONString(result);
                                return dataBufferFactory.wrap(wrappedResponse.getBytes(StandardCharsets.UTF_8));
                            }
                        } catch (Exception e) {
                            // 解析失败，可能不是 JSON，直接返回
                            log.error("响应解析失败：{}", e.getMessage(), e);
                            return dataBufferFactory.wrap(content);
                        }
                    }));
                } else {
                    // 非 JSON 响应，直接返回
                    return super.writeWith(body);
                }
            }
        };

        // 继续过滤链
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 判断是否是 JSON 响应
     */
    private boolean isJsonResponse(ServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null && (contentType.includes(MediaType.APPLICATION_JSON) || contentType.toString().contains("json"));
    }

    @Override
    public int getOrder() {
        // 优先级设置为 -1，确保在其他过滤器之后执行
        return -1;
    }
}
