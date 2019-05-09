package com.bawer.tasks.revolut.ewallet.model

import com.bawer.tasks.revolut.ewallet.disruptor.TransferEvent
import java.math.BigDecimal
import java.time.ZonedDateTime

data class Transfer (
        val id: Long,
        val type: TransferType,
        val description: String?,
        val sourceAccountId: Int?,
        val targetAccountId: Int,
        val amount: BigDecimal,
        val targetDate: ZonedDateTime?,
        val receiveTimestamp: Long,
        val completionTimestamp: Long
) {

    var status: TransferStatus = TransferStatus.DRAFT
        private set

    fun setFailedStatus() { status = TransferStatus.FAILED }

    fun setCompletedStatus() { status = TransferStatus.COMPLETED }

    companion object {
        fun from(event: TransferEvent, status: TransferStatus) = Transfer(
                id = event.request.id,
                type = event.request.type,
                description = event.request.description,
                sourceAccountId = event.request.sourceId,
                targetAccountId = event.request.targetId,
                amount = event.request.amount,
                targetDate = event.request.targetDate,
                receiveTimestamp = event.receiveTimestamp,
                completionTimestamp = System.currentTimeMillis()
        ).apply { this.status = status }
    }
}