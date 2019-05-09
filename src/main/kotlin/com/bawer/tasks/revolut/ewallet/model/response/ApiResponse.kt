package com.bawer.tasks.revolut.ewallet.model.response

class ApiResponse <T> (
        val returnObject: T? = null,
        val status: String = "OK",
        val errorDescription: String? = null
) {

    companion object {

        fun notFound(description: String? = null) = ApiResponse(null, "NOT_FOUND", description)

        fun notCancellable(description: String? = null) = ApiResponse(null, "NOT_CANCELLABLE", description)

        fun failed(description: String? = null) = ApiResponse(null, "FAILED", description)
    }
}