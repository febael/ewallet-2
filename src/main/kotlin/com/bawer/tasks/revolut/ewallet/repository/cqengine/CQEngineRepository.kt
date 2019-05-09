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

    override fun insert(obj: T) = collection.add(obj)

    override fun insertAll(vararg objs: T) = collection.addAll(objs)

    /**
     * CQEngine doesn't need updates, do nothing
     */
    override fun upsert(obj: T) = true

    /**
     * CQEngine doesn't need updates, do nothing
     */
    override fun upsertAll(vararg objs: T) = true

    override fun count() = collection.size

    override fun delete(id: I) = get(id)?.let { collection.remove(it) } ?: false

    override fun deleteAll(vararg ids: I) = TODO("not implemented")
}