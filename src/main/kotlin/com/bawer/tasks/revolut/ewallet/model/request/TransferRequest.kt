package com.bawer.tasks.revolut.ewallet.model.request

import com.bawer.tasks.revolut.ewallet.ERROR_SOURCE_ID_REQUIRED
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.TransferType
import com.bawer.tasks.revolut.ewallet.model.TransferType.INTERNAL
import com.bawer.tasks.revolut.ewallet.model.exception.InvalidRequestException
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.ZonedDateTime

data class TransferRequest (
        val type: TransferType,
        val description: String? = null,
        @SerializedName("sourceId") private val unvalidatedSourceId: Int? = null,
        val targetId: Int,
        val amount: BigDecimal,
        val targetDate: ZonedDateTime? = null,
        var status: TransferStatus = TransferStatus.DRAFT
) {

    var id: Long = Long.MIN_VALUE

    val sourceId: Int?

    init { // validate sourceId here
        if (type == INTERNAL && unvalidatedSourceId == null) throw InvalidRequestException(ERROR_SOURCE_ID_REQUIRED)
        else sourceId = unvalidatedSourceId
    }

    val immediate: Boolean by lazy { targetDate?.run { false } ?: true }
}