package com.bawer.tasks.revolut.ewallet.repository

interface Repository <T, I> {

    fun getId(obj: T): I

    fun get(id: I): T?

    fun getAll(): List<T>

    fun insert(obj: T): Boolean

    fun insertAll(vararg objs: T): Boolean

    fun upsert(obj: T): Boolean

    fun upsertAll(vararg objs: T): Boolean

    fun delete(id: I): Boolean

    fun deleteAll(vararg ids: I): Boolean

    fun count(): Int
}