package com.bloom.aiproxy

import org.springframework.stereotype.Component
import java.time.Clock
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimiter(
    private val properties: AiProxyProperties,
) {
    private val clock: Clock = Clock.systemUTC()
    private val buckets = ConcurrentHashMap<String, Bucket>()

    fun allow(clientId: String): Boolean {
        if (properties.rateLimitRequests <= 0 || properties.rateLimitWindowSeconds <= 0) return true

        val now = clock.millis()
        val windowMillis = properties.rateLimitWindowSeconds * 1_000
        val bucket = buckets.compute(clientId) { _, current ->
            if (current == null || now - current.windowStartedAtMillis >= windowMillis) {
                Bucket(windowStartedAtMillis = now, count = 1)
            } else {
                current.copy(count = current.count + 1)
            }
        }
        return (bucket?.count ?: 0) <= properties.rateLimitRequests
    }

    private data class Bucket(
        val windowStartedAtMillis: Long,
        val count: Int,
    )
}
