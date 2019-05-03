package com.bawer.tasks.revolut.ewallet.response

data class TransferResponse (
        val id: Int,
        val checkURI: String,
        val cancelURI: String
)