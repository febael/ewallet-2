package com.bawer.tasks.revolut.ewallet.repository

interface Repository <T, I> {

    fun get(id: I): T?

    fun getAll(): List<T>

    fun save(obj: T): Boolean
}