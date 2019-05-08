package com.bawer.tasks.revolut.ewallet.model

import com.bawer.tasks.revolut.ewallet.request.AccountRequest
import java.math.BigDecimal

data class Account (
        val id: Int,
        val holderName: String,
        val holderSurname: String,
        val currency: Currency,
        val transfers: ArrayList<Transfer> = ArrayList()
) {

    var balance: BigDecimal = BigDecimal.ZERO

    companion object {
        fun from(request: AccountRequest, id: Int) = Account(
                id = id,
                holderName = request.holderName,
                holderSurname = request.holderSurname,
                currency = request.currency
        ).apply { balance = request.balance }
    }
}