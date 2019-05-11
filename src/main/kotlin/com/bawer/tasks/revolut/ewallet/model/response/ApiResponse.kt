package com.bawer.tasks.revolut.ewallet.model.response

import com.bawer.tasks.revolut.ewallet.STATUS_FAILED
import com.bawer.tasks.revolut.ewallet.STATUS_NOT_CANCELLABLE
import com.bawer.tasks.revolut.ewallet.STATUS_NOT_FOUND
import com.bawer.tasks.revolut.ewallet.STATUS_OK

class ApiResponse <T> (
        val returnObject: T? = null,
        val status: String = STATUS_OK,
        val errorDescription: String? = null
) {

    companion object {

        fun notFound(description: String? = null) = ApiResponse(null, STATUS_NOT_FOUND, description)

        fun notCancellable(description: String? = null) = ApiResponse(null, STATUS_NOT_CANCELLABLE, description)

        fun failed(description: String? = null) = ApiResponse(null, STATUS_FAILED, description)
    }
}