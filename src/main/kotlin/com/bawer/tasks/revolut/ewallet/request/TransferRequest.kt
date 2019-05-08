package com.bawer.tasks.revolut.ewallet.request

import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.TransferType
import java.math.BigDecimal
import java.time.ZonedDateTime

data class TransferRequest (
        val type: TransferType,
        val description: String? = null,
        val sourceAccountId: Int? = null,
        val targetAccountId: Int,
        val amount: BigDecimal,
        val targetDate: ZonedDateTime? = null,
        var status: TransferStatus = TransferStatus.DRAFT
) {
    var id: Long = Long.MIN_VALUE

    val immediate: Boolean by lazy { targetDate?.run { false } ?: true }
}