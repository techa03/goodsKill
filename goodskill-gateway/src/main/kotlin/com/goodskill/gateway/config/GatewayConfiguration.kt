package com.goodskill.gateway.config

import com.goodskill.gateway.filter.JwtAuthGatewayFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import java.time.ZonedDateTime


@Configuration
open class GatewayConfiguration {
    @Autowired
    private lateinit var jwtAuthGatewayFilter: JwtAuthGatewayFilter

    @Bean
    @Order
    open fun customizedLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route { r: PredicateSpec ->
                r.after(ZonedDateTime.now().plusSeconds(20L)).and()
                    .path("/goodskill/es/**")
                    // 路径根据"/"分割符去掉前几位
                    .filters { f: GatewayFilterSpec ->
                        f.stripPrefix(2).filter(jwtAuthGatewayFilter)
                    }
                    // 访问的负载均衡地址
                    .uri("lb://goodskill-es-provider")
            }
            .route { r: PredicateSpec ->
                r.after(ZonedDateTime.now().plusSeconds(6L)).and()
                    .path("/goodskill/mongo/**")
                    .filters { f: GatewayFilterSpec ->
                        f.stripPrefix(2).filter(jwtAuthGatewayFilter)
                    }
                    .uri("lb://mongo-service-provider").id("mongo")
            }
            .build()
    }

}