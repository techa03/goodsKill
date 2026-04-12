package com.goodskill.core.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class FeignHeaderInterceptor implements RequestInterceptor {

    private static final Set<String> EXCLUDED_HEADERS = new HashSet<>(Arrays.asList(
            "content-length",
            "content-type",
            "transfer-encoding",
            "connection",
            "keep-alive",
            "te",
            "trailer",
            "upgrade",
            "host",
            "accept",
            "accept-encoding",
            "cache-control",
            "pragma"
    ));

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String lowerCaseName = name.toLowerCase();

                    if (!EXCLUDED_HEADERS.contains(lowerCaseName)) {
                        String values = request.getHeader(name);
                        template.header(name, values);
                    }
                }
            }
        }
    }
}
