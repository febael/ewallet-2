package com.bawer.tasks.revolut.ewallet.repository

import com.bawer.tasks.revolut.ewallet.model.Account
import java.math.BigDecimal

interface  AccountRepository: Repository<Account, Int> {

    val saveAllAfterInternalTransfer: Boolean

    /**
     * Some implementations may reduce round-trip to server.
     */
    fun getTwo(id1: Int, id2: Int): Pair<Account?, Account?> = Pair(get(id1), get(id2))

    /**
     * If this keeps the system in fully consistent state depends on implementing class
     * (Note that concurrency issues are handled by lmax disruptor)
     */
    fun internalTransfer(fromId: Int, toId: Int, amount: BigDecimal): Boolean {
        val (fromAccount, toAccount) = getTwo(fromId, toId)
        fromAccount ?: return false
        toAccount ?: return false
        return if (fromAccount.balance >= amount) {
            fromAccount.balance -= amount
            toAccount.balance += amount
            if (saveAllAfterInternalTransfer) saveAll(fromAccount, toAccount) else true
        } else false
    }
}
