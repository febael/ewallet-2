package com.bawer.tasks.revolut.ewallet.test

import com.bawer.tasks.revolut.ewallet.repository.redis.RedisAccountRepository
import com.bawer.tasks.revolut.ewallet.repository.redis.RedisTransferRepository
import com.bawer.tasks.revolut.ewallet.repository.redis.RedisWrapper
import com.google.gson.Gson
import org.junit.jupiter.api.AfterAll


object RedisFunctionalTests : BaseFunctionalTests() {

    private val gson = Gson()
    private val jedisPool = RedisWrapper.jedisPool

    override val accountRepository = RedisAccountRepository(jedisPool, gson)
    override val transferRepository = RedisTransferRepository(jedisPool, gson)

    @AfterAll
    override fun tearDown() {
        RedisWrapper.tearDown()
        super.tearDown()
    }
}