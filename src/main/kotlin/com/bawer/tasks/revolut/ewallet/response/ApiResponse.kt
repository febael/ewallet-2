package com.bawer.tasks.revolut.ewallet.response

class ApiResponse <T> (
        val returnObject: T? = null,
        val status: String = "OK",
        val errorDescription: String? = null
) {

    companion object {

        fun notFound(description: String? = null) = ApiResponse(null, "NOT_FOUND", description)

        fun notCancellable(description: String? = null) = ApiResponse(null, "NOT_FOUND", description)
    }
}