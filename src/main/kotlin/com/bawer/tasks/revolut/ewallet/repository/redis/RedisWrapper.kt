package com.bawer.tasks.revolut.ewallet.repository.redis

import com.bawer.tasks.revolut.ewallet.myLogger
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.embedded.RedisServer

object RedisWrapper {

    private val logger = myLogger()

    private var serverInstance: RedisServer? = null

    private var jedisPoolInstance: JedisPool? = null

    var jedisPool: JedisPool? = null
        private set
        get() {
            if (serverInstance == null) {
                create()
            }
            return jedisPoolInstance?.takeUnless { it.isClosed }
                    ?: JedisPool(poolConfig).also { jedisPoolInstance = it }
        }

    private val poolConfig by lazy {
        JedisPoolConfig().apply {
            maxTotal = 32
            maxIdle = 32
            minIdle = 2
            testOnBorrow = false
            testOnReturn = false
            testWhileIdle = false
            minEvictableIdleTimeMillis = 0
            timeBetweenEvictionRunsMillis = 0
            blockWhenExhausted = true
        }
    }

    fun create(port: Int = 6379): RedisServer {
        logger.info("create for Redis")
        serverInstance = RedisServer(port).apply { start() }
        return serverInstance as RedisServer
    }

    fun tearDown() {
        logger.info("tearDown for Redis")
        jedisPool?.apply { close(); jedisPool = null }
        serverInstance?.apply { if(isActive) stop(); serverInstance = null }
    }
}
