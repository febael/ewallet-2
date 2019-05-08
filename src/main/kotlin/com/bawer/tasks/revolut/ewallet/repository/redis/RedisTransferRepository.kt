package com.bawer.tasks.revolut.ewallet.repository.redis

import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.repository.TransferRepository
import com.google.gson.Gson
import redis.clients.jedis.JedisPool
import javax.inject.Inject


class RedisTransferRepository @Inject constructor(
        jedisPool: JedisPool,
        gson: Gson
) : TransferRepository, RedisRepository<Transfer, Long>(Transfer::class.java, jedisPool, gson, "Transfers") {

    override fun getId(obj: Transfer) = obj.id

    /**
     * Not trivial with Redis (multiple collections?)
     */
    override fun getAll(status: TransferStatus) = TODO("not implemented")
}