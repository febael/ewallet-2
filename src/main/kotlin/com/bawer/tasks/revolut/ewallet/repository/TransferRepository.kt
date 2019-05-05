package com.bawer.tasks.revolut.ewallet.repository

import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.TransferStatus

interface TransferRepository : Repository<Transfer, Long> {

    fun getAll(status: TransferStatus): List<Transfer>
}