package com.bawer.tasks.revolut.ewallet.service

import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.response.TransferResponse

interface TransferService {
    fun getAll(status: TransferStatus? = null): List<Transfer>
    fun get(id: Int): Transfer
    fun create(request: TransferRequest): TransferResponse
    fun cancel(id: Int)
}