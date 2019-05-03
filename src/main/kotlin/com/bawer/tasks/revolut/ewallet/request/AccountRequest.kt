package com.bawer.tasks.revolut.ewallet.request

import com.bawer.tasks.revolut.ewallet.model.Currency
import java.math.BigDecimal

data class AccountRequest (
        val holderName: String,
        val holderSurname: String,
        val currency: Currency,
        val balance: BigDecimal = BigDecimal.ZERO
)