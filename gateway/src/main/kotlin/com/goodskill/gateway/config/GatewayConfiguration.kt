package com.goodskill.gateway.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

@Configuration
open class GatewayConfiguration {

    @Bean
    @Order
    open fun customizedLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
                .route { r: PredicateSpec ->
                    r.path("/*/seckill/es/**")
                            .filters { f: GatewayFilterSpec -> f.stripPrefix(3) }
                            .uri("lb://es-service-provider")
                }.build()
    }
}