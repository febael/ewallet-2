package com.bawer.tasks.revolut.ewallet.service.impl

import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.bawer.tasks.revolut.ewallet.request.AccountRequest
import com.bawer.tasks.revolut.ewallet.request.TransferDirection
import com.bawer.tasks.revolut.ewallet.service.AccountService
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class AccountServiceImpl(@Inject private val repository: AccountRepository) : AccountService {

    private val idGenerator = AtomicInteger(0)

    override fun getAll() = repository.getAll()

    override fun create(request: AccountRequest) = Account.from(request, idGenerator.incrementAndGet()).apply {
            repository.save(this)
    }

    override fun get(id: Int) = repository.get(id)

    override fun getTransfers(id: Int, direction: TransferDirection, limit: Int, after: Int) =
        TODO("not implemented")
}