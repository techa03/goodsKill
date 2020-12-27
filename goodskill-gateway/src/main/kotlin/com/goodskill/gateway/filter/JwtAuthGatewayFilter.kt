package com.goodskill.gateway.filter

import com.goodskill.common.JwtUtils
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


/**
 * 用户身份验证过滤器
 * @author techa03
 * @since 2020/12/26
 */
@Component
class JwtAuthGatewayFilter: GatewayFilter {
    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        JwtUtils.parseToken(exchange!!.request.headers["token"]!![0].toString())
        return chain!!.filter(exchange)
    }
}