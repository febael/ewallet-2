package com.bawer.tasks.revolut.ewallet.response

data class ApiResponse <T> (
        val returnObject: T? = null,
        val status: String = "OK",
        val errorDescription: String? = null
)