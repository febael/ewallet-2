package com.bawer.tasks.revolut.ewallet.repository.cqengine

import com.bawer.tasks.revolut.ewallet.repository.Repository
import com.googlecode.cqengine.IndexedCollection
import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.googlecode.cqengine.resultset.common.NoSuchObjectException

abstract class CQEngineRepository<T, I> : Repository<T, I> {

    internal abstract val collection: IndexedCollection<T>

    internal abstract val idAttribute : SimpleAttribute<T, I>

    open fun initialize() = collection.addIndex( UniqueIndex.onAttribute(idAttribute) )

    override fun get(id: I) = try {
        collection.retrieve( equal(idAttribute, id) ).uniqueResult()
    } catch (e: NoSuchObjectException) {
        null
    }

    override fun getAll(): List<T> = collection.toList()

    override fun save(obj: T) = collection.add(obj)

    // TODO : Creation of a list object overhead at addAll
    override fun saveAll(vararg objs: T) = collection.addAll(objs)

    override fun count() = collection.size

    override fun delete(id: I) = TODO("not implemented")

    override fun deleteAll(vararg ids: I) = TODO("not implemented")
}