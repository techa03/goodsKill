package com.goodskill.core.tracing;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 将当前请求的 trace id 写入响应头 X-Trace-Id,便于前端/客户端在报错时附带,方便通过 Tempo / Loki 关联日志与追踪.
 * <p>仅在 Servlet Web 环境下激活(配合 @ConditionalOnWebApplication),与 WebFlux 版本互不冲突.
 * 参考: https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final Tracer tracer;

    public TraceIdFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = currentTraceId();
        if (traceId != null) {
            response.setHeader(TRACE_ID_HEADER, traceId);
        }
        filterChain.doFilter(request, response);
    }

    private String currentTraceId() {
        var context = tracer.currentTraceContext().context();
        return context != null ? context.traceId() : null;
    }
}
