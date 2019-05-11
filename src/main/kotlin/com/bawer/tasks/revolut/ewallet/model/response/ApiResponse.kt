package com.bawer.tasks.revolut.ewallet.model.response

import com.bawer.tasks.revolut.ewallet.STATUSTEXT_FAILED
import com.bawer.tasks.revolut.ewallet.STATUSTEXT_NOT_CANCELLABLE
import com.bawer.tasks.revolut.ewallet.STATUSTEXT_NOT_FOUND
import com.bawer.tasks.revolut.ewallet.STATUSTEXT_OK
import com.bawer.tasks.revolut.ewallet.model.TransferStatus

class ApiResponse <T> (
        val returnObject: T? = null,
        val status: String = STATUSTEXT_OK,
        val errorDescription: String? = null
) {

    companion object {

        fun notFound(description: String? = null) = ApiResponse(null, STATUSTEXT_NOT_FOUND, description)

        fun notCancellable(description: String? = null, status: TransferStatus) =
                ApiResponse(status, STATUSTEXT_NOT_CANCELLABLE, description)

        fun failed(description: String? = null) = ApiResponse(null, STATUSTEXT_FAILED, description)
    }
}