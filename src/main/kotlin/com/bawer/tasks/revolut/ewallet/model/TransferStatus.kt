package com.bawer.tasks.revolut.ewallet.model

enum class TransferStatus {
    DRAFT,
    RECEIVED,
    PROCESSING,
    CANCELLED,
    FAILED,
    COMPLETED
}