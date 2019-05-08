package com.bawer.tasks.revolut.ewallet.repository.redis

import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.google.gson.Gson
import com.sun.org.apache.xpath.internal.operations.Bool
import redis.clients.jedis.JedisPool
import java.math.BigDecimal
import javax.inject.Inject


class RedisAccountRepository @Inject constructor(
        jedisPool: JedisPool,
        gson: Gson
) : AccountRepository, RedisRepository<Account, Int>(Account::class.java, jedisPool, gson, "accounts") {

    override val saveAllAfterInternalTransfer = true

    override fun getId(obj: Account) = obj.id

    override fun getTwo(id1: Int, id2: Int) = jedis.hmget(hashName, id1.toString(), id2.toString()).let {
        Pair(deserialize(it[0]), deserialize(it[1]))
    }
}