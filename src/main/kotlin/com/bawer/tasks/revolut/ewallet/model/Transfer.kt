package com.bawer.tasks.revolut.ewallet.model

import com.bawer.tasks.revolut.ewallet.disruptor.TransferEvent
import java.math.BigDecimal
import java.time.ZonedDateTime

class Transfer private constructor(
        val id: Long,
        val type: TransferType,
        val description: String?,
        val sourceAccountId: Int?,
        val targetAccountId: Int,
        val amount: BigDecimal,
        val targetDate: ZonedDateTime?,
        val receiveTimestamp: Long,
        val completionTimestamp: Long,
        val status: TransferStatus
) {

    companion object {
        fun from(event: TransferEvent, status: TransferStatus) = Transfer(
                id = event.request.id,
                type = event.request.type,
                description = event.request.description,
                sourceAccountId = event.request.sourceAccountId,
                targetAccountId = event.request.targetAccountId,
                amount = event.request.amount,
                targetDate = event.request.targetDate,
                receiveTimestamp = event.receiveTimestamp,
                completionTimestamp = System.currentTimeMillis(),
                status = status
        )
    }
}