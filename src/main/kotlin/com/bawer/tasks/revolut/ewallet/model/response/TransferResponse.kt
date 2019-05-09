package com.bawer.tasks.revolut.ewallet.model.response

import com.bawer.tasks.revolut.ewallet.model.TransferStatus

data class TransferResponse (
        val id: Long,
        val status: TransferStatus? = null,
        val checkURI: String,
        val cancelURI: String? = null
)