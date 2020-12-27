package com.goodskill.gateway.filter.factory

import com.goodskill.common.JwtUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

@Component
class JwtAuthGatewayFilterFactory :
    AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config>(Config::class.java) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun apply(config: Config): GatewayFilter {
        // grab configuration from Config object
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            JwtUtils.parseToken(exchange.request.headers["token"]!![0].toString())
            chain.filter(exchange)
        }
    }

    class Config { //Put the configuration properties for your filter here
    }

}