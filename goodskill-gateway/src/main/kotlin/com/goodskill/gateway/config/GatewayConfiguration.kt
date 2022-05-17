package com.goodskill.gateway.config

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import java.util.stream.Collectors


@Configuration
open class GatewayConfiguration {
    @Bean
    open fun customizedLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("es-service-provider") { r: PredicateSpec ->
                r.path("/goodskill/es/**")
                    // 路径根据"/"分割符去掉前几位
                    .filters { f: GatewayFilterSpec ->
                        f.stripPrefix(2)
                    }
                    // 访问的负载均衡地址
                    .uri("lb://es-service-provider")
            }
            .route("mongo-service-provider") { r: PredicateSpec ->
                r.path("/goodskill/mongo/**")
                    .filters { f: GatewayFilterSpec ->
                        f.stripPrefix(2)
                    }
                    .uri("lb://mongo-service-provider")
            }
            .route("goodskill-seata") { r: PredicateSpec ->
                r.path("/goodskill/seata/**")
                    .filters { f: GatewayFilterSpec ->
                        f.stripPrefix(2)
                    }
                    .uri("lb://goodskill-seata")
            }
            .route("goodskill-service-provider") { r: PredicateSpec ->
                r.path("/goodskill/common/**")
                    .filters { f: GatewayFilterSpec ->
                        f.stripPrefix(2)
                    }
                    .uri("lb://goodskill-service-provider")
            }
            .route("goodskill-web") { r: PredicateSpec ->
                r.path("/goodskill/web/**")
                    .uri("lb://goodskill-web")
            }
            .build()
    }

    /**
     * 修复使用feign client远程调用服务时报HttpMessageConverters缺失问题
     */
    @Bean
    @ConditionalOnMissingBean
    open fun messageConverters(converters: ObjectProvider<HttpMessageConverter<*>?>): HttpMessageConverters? {
        return HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()))
    }

}