package com.bawer.tasks.revolut.ewallet.repository.cqengine

import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.query.option.QueryOptions


class CQEngineAccountRepository : AccountRepository, CQEngineRepository<Account, Int>() {

    override val saveAllAfterInternalTransfer = false

    override val collection = ConcurrentIndexedCollection<Account>()

    override val idAttribute = object : SimpleAttribute<Account, Int>(Account::id.name) {
        override fun getValue(account: Account?, queryOptions: QueryOptions?) = account!!.id
    }

    init {
        initialize()
    }
}