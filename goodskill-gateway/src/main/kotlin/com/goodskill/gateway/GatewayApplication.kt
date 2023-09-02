package com.goodskill.gateway

import com.goodskill.common.core.feign.AuthService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackageClasses = [AuthService::class])
open class GatewayApplication

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}
