package com.bawer.tasks.revolut.ewallet.model

import java.math.BigDecimal

data class Account (
        val id: Int,
        val holderName: String,
        val holderSurname: String,
        val currency: Currency,
        val balance: BigDecimal = BigDecimal.ZERO
)