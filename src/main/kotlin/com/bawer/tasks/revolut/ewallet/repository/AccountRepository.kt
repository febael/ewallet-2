package com.bawer.tasks.revolut.ewallet.repository

import com.bawer.tasks.revolut.ewallet.model.Account

interface AccountRepository: Repository<Account, Int> {

    /**
     * Some implementations may reduce round-trip to server.
     */
    fun getTwo(id1: Int, id2: Int): Pair<Account?, Account?> = Pair(get(id1), get(id2))
}
