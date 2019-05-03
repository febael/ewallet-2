package com.bawer.tasks.revolut.ewallet.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class Transfer (
        val id: Int,
        val type: TransferType,
        val description: String? = null,
        val sourceAccountId: Int? = null,
        val targetAccountId: Int,
        val amount: BigDecimal,
        val targetDate: ZonedDateTime? = null,
        val receiveTimestamp: Long,
        val completionTimestamp: Long? = null,
        val status: TransferStatus
)