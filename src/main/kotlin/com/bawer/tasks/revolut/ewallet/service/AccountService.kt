package com.bawer.tasks.revolut.ewallet.service

import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.request.AccountRequest
import com.bawer.tasks.revolut.ewallet.request.TransferDirection

interface AccountService {
    fun getAll(): List<Account>
    fun create(request: AccountRequest): Account
    fun get(id: Int): Account
    fun getTransfers(id: Int, direction: TransferDirection, limit: Int, after: Int): List<Transfer>
}