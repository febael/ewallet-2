package com.bawer.tasks.revolut.ewallet.repository

interface Repository <T, I> {

    fun get(id: I): T?

    fun getAll(): List<T>

    fun save(obj: T): Boolean

    fun saveAll(vararg objs: T): Boolean

    fun delete(id: I): Boolean

    fun deleteAll(vararg ids: I): Boolean

    fun count(): Int
}