package com.bloom.aiproxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AiProxyProperties::class)
class AiProxyApplication

fun main(args: Array<String>) {
    runApplication<AiProxyApplication>(*args)
}
