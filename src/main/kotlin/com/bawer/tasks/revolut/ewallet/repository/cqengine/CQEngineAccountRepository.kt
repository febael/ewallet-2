package com.bawer.tasks.revolut.ewallet.repository.cqengine

import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.googlecode.cqengine.TransactionalIndexedCollection
import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.query.option.QueryOptions


class CQEngineAccountRepository : AccountRepository, CQEngineRepository<Account, Int>() {

    override val collection = TransactionalIndexedCollection(Account::class.java)

    override val idAttribute = object : SimpleAttribute<Account, Int>(Account::id.name) {
        override fun getValue(account: Account?, queryOptions: QueryOptions?) = account!!.id
    }

    init {
        initialize()
    }
}