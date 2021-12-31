package com.goodskill.gateway.filter.factory

import com.goodskill.api.service.AuthService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.web.server.ServerWebExchange

/**
 * 过滤器工厂，可应用于配置文件中，使用JwtAuth前缀配置，使用时需要注入spring容器
 */
//@Component
class JwtAuthGatewayFilterFactory :
    AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config>(Config::class.java) {

    @Autowired
    private lateinit var authService: AuthService

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun apply(config: Config): GatewayFilter {
        // grab configuration from Config object
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val token = exchange.request.headers["token"]!![0].toString()
            val result = try {
                authService.verifyToken(token)
            } catch (e: Exception) {
                throw RuntimeException("auth service is not available!")
            }
            if (result.code != "200") {
                throw RuntimeException(result.message)
            }
            chain.filter(exchange)
        }
    }

    class Config { //Put the configuration properties for your filter here
    }

}