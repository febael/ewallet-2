package com.bawer.tasks.revolut.ewallet.model

import com.bawer.tasks.revolut.ewallet.request.AccountRequest
import java.math.BigDecimal

data class Account (
        val id: Int,
        val holderName: String,
        val holderSurname: String,
        val currency: Currency,
        val balance: BigDecimal = BigDecimal.ZERO,
        val transfers: ArrayList<Transfer> = ArrayList()
) {

    companion object {
        fun from(accountRequest: AccountRequest, id: Int) = Account(
                id = id,
                holderName = accountRequest.holderName,
                holderSurname = accountRequest.holderSurname,
                currency = accountRequest.currency,
                balance = accountRequest.balance
        )
    }
}