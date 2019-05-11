package com.bawer.tasks.revolut.ewallet.model.request

import com.bawer.tasks.revolut.ewallet.ERROR_INVALID_AMOUNT
import com.bawer.tasks.revolut.ewallet.ERROR_INVALID_TARGET_DATE
import com.bawer.tasks.revolut.ewallet.ERROR_SOURCE_EQUALS_TARGET
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
        @SerializedName("description") private val unvalidatedDescription: String? = null,
        @SerializedName("optionalSourceId") private val unvalidatedSourceId: Int? = null,
        val targetId: Int,
        val amount: BigDecimal,
        val targetDate: ZonedDateTime? = null
) {

    var id: Long = Long.MIN_VALUE

    var status: TransferStatus = TransferStatus.DRAFT

    val sourceId: Int?

    val reviewedDescription: String

    init { // validate here
        if (type == INTERNAL) when (unvalidatedSourceId) {
            null -> throw InvalidRequestException(ERROR_SOURCE_ID_REQUIRED)
            targetId -> throw InvalidRequestException(ERROR_SOURCE_EQUALS_TARGET)
        }
        if (amount <= BigDecimal.ZERO) throw InvalidRequestException(ERROR_INVALID_AMOUNT)
        // TODO : add here amount maximum valid check
        if (targetDate != null && targetDate.isBefore(ZonedDateTime.now().plusMinutes(10))) {
            throw InvalidRequestException(ERROR_INVALID_TARGET_DATE)
        }
        reviewedDescription = unvalidatedDescription?.substring(0, MAX_DESCRIPTION_LENGTH) ?: ""
        sourceId = unvalidatedSourceId
    }

    @Transient
    val isImmediate = targetDate == null

    companion object {
        private const val MAX_DESCRIPTION_LENGTH = 500
    }
}