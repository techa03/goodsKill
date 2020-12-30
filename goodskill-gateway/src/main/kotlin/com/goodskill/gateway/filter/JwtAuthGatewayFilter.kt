package com.goodskill.gateway.filter

import org.seckill.api.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


/**
 * 用户身份验证过滤器
 * @author techa03
 * @since 2020/12/26
 */
@Component
class JwtAuthGatewayFilter: GatewayFilter, Ordered {
    @Autowired
    private lateinit var authService: AuthService

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val token = exchange.request.headers["token"]!![0].toString()
        val result = try {
            authService.verifyToken(token)
        } catch (e: Exception) {
            throw RuntimeException("auth service is not available!")
        }
        if (result.code != "200") {
            throw RuntimeException(result.message)
        }
        return chain.filter(exchange)
    }

    override fun getOrder(): Int {
        return 0
    }
}