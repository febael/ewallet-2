package com.bawer.tasks.revolut.ewallet.repository.cqengine

import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.repository.TransferRepository
import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.index.hash.HashIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.googlecode.cqengine.query.option.QueryOptions


class CQEngineTransferRepository : TransferRepository, CQEngineRepository<Transfer, Long>() {

    override val collection = ConcurrentIndexedCollection<Transfer>()

    override val idAttribute = object : SimpleAttribute<Transfer, Long>(Transfer::id.name) {
        override fun getValue(transfer: Transfer?, queryOptions: QueryOptions?) = transfer!!.id
    }

    private val statusAttribute = object : SimpleAttribute<Transfer, TransferStatus>(Transfer::status.name) {
        override fun getValue(transfer: Transfer?, queryOptions: QueryOptions?) = transfer!!.status
    }

    init {
        collection.addIndex( HashIndex.onAttribute(statusAttribute) )
        initialize()
    }

    override fun getAll(status: TransferStatus)= collection.retrieve( equal(statusAttribute, status) ).toList()

    override fun getId(obj: Transfer) = obj.id
}