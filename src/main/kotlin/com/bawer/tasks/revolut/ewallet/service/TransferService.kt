package com.bawer.tasks.revolut.ewallet.service

import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest

interface TransferService {

    fun getAll(status: TransferStatus? = null): List<Transfer>

    fun get(id: Long): TransferStatus?

    fun create(request: TransferRequest): Long

    fun cancel(id: Long): TransferStatus?
}