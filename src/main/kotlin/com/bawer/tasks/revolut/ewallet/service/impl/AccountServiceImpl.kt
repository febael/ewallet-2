package com.bawer.tasks.revolut.ewallet.service.impl

import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.bawer.tasks.revolut.ewallet.model.request.AccountRequest
import com.bawer.tasks.revolut.ewallet.model.request.TransferDirection
import com.bawer.tasks.revolut.ewallet.service.AccountService
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class AccountServiceImpl @Inject constructor(private val repository: AccountRepository) : AccountService {

    private val idGenerator = AtomicInteger(0)

    private val nextId get() = idGenerator.incrementAndGet()

    override fun getAll() = repository.getAll()

    override fun create(request: AccountRequest) = Account.from(request, nextId).apply { repository.insert(this) }

    /**
     * TODO : nullify transfers, they should be requested with [getTransfers]
     */
    override fun get(id: Int) = repository.get(id)

    override fun getTransfers(id: Int, direction: TransferDirection, limit: Int, after: Int) = TODO("not implemented")
}