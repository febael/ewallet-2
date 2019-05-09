package com.bawer.tasks.revolut.ewallet.disruptor

import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.TransferType
import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest
import java.math.BigDecimal

class TransferEvent {
    var receiveTimestamp: Long = Long.MIN_VALUE
        private set
    var request: TransferRequest = DUMMY_TRANSFER_REQUEST
        private set

    fun updateFrom(request: TransferRequest) {
        this.receiveTimestamp = System.currentTimeMillis()
        this.request = request.apply { status = TransferStatus.RECEIVED }
    }

    companion object {
        private val DUMMY_TRANSFER_REQUEST = TransferRequest(
                type = TransferType.DEPOSIT,
                targetId = Int.MIN_VALUE,
                amount = BigDecimal.ZERO
        )
    }
}