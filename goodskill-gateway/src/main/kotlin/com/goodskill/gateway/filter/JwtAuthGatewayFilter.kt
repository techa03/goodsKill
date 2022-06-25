package com.goodskill.gateway.filter

import com.goodskill.api.dto.AuthResponseDTO
import com.goodskill.api.service.AuthService
import com.goodskill.gateway.config.IgnoreProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.util.AntPathMatcher
import org.springframework.util.CollectionUtils
import org.springframework.util.PathMatcher
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
    @Autowired
    private lateinit var ignoreProperties: IgnoreProperties
    private val pathMatcher: PathMatcher = AntPathMatcher()

    private val fixedThreadPool = Executors.newFixedThreadPool(10)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        // 跳过白名单url
        if (!CollectionUtils.isEmpty(ignoreProperties.whiteUrl)) {
            ignoreProperties.whiteUrl.forEach() {
                if (pathMatcher.match(it, exchange.request.path.toString())) {
                    return chain.filter(exchange)
                }
            }
        }
        var token = exchange.request.headers["Authorization"]!![0].toString()
        if (token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "")
            println(token)
        }
        // FIXME 由于springcloud 2020.0.x不支持nio线程远程调用，此处使用线程池执行
        var result = AuthResponseDTO()
        val future = fixedThreadPool.submit<AuthResponseDTO> {
            result = try {
                val authResponseDTO = authService.verifyToken(token)
                authResponseDTO
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
        val serverHttpRequest = exchange.request.mutate()
            .header("user_id", result.userId)
            .header("user_name", result.userName)
            .header("user_fullname", null)
            .build()
        return chain.filter(exchange.mutate().request(serverHttpRequest).build())
    }

    override fun getOrder(): Int {
        return 0
    }
}