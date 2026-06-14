package com.goodskill.core.tracing;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux 版本: 将当前请求的 trace id 写入响应头 X-Trace-Id.
 * <p>仅在 Reactive Web 环境下激活(配合 @ConditionalOnWebApplication),与 Servlet 版本互不冲突.
 * 使用 Ordered.HIGHEST_PRECEDENCE 与 goodskill-gateway 的 RequestLoggingFilter 一致,确保最先进入.
 * 参考: https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdWebFilter implements WebFilter {

    private final Tracer tracer;

    public TraceIdWebFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    var context = tracer.currentTraceContext().context();
                    if (context != null) {
                        exchange.getResponse().getHeaders().set(TraceIdFilter.TRACE_ID_HEADER, context.traceId());
                    }
                }));
    }
}
