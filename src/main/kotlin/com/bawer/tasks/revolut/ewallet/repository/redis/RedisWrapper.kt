package com.bawer.tasks.revolut.ewallet.repository.redis

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.embedded.RedisServer

object RedisWrapper {

    private var instance: RedisServer? = null

    private val jedisPoolDelegate = lazy {
        instance ?: create()
        JedisPool(poolConfig)
    }

    val jedisPool by jedisPoolDelegate

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
        tearDown()
        instance = RedisServer(port).apply { start() }
        return instance as RedisServer
    }

    fun tearDown() {
        if (jedisPoolDelegate.isInitialized()) {
            jedisPool.close()
        }
        instance?.apply { if(isActive) stop() } ?: return
        instance = null
    }
}
