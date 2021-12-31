package com.goodskill.gateway.filter

import com.goodskill.api.dto.AuthResponseDTO
import com.goodskill.api.service.AuthService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * 用户身份验证过滤器
 * @author techa03
 * @since 2020/12/26
 */
//@Component
class JwtAuthGatewayFilter: GlobalFilter, Ordered {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authService: AuthService

    private val fixedThreadPool = Executors.newFixedThreadPool(10)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val token = exchange.request.headers["token"]!![0].toString()
        // FIXME 由于springcloud 2020.0.x不支持nio线程远程调用，此处使用线程池执行
        val future = fixedThreadPool.submit<AuthResponseDTO> {
            val result = try {
                authService.verifyToken(token)
            } catch (e: Exception) {
                throw RuntimeException("auth service is not available!")
            }
            if (result.code != "200") {
                log.warn(result.message)
                throw RuntimeException(result.message)
            }
            result
        }

        future.get(3L, TimeUnit.SECONDS)
        return chain.filter(exchange)
    }

    override fun getOrder(): Int {
        return 0
    }
}