package com.bawer.tasks.revolut.ewallet.controller

import com.bawer.tasks.revolut.ewallet.STATUS_ACCEPTED
import com.bawer.tasks.revolut.ewallet.STATUS_BAD_REQUEST
import com.bawer.tasks.revolut.ewallet.STATUS_NOT_FOUND
import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.TransferStatus.*
import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.model.response.ApiResponse
import com.bawer.tasks.revolut.ewallet.model.response.TransferResponse
import com.bawer.tasks.revolut.ewallet.service.TransferService
import ro.pippo.controller.*
import ro.pippo.controller.extractor.Body
import ro.pippo.controller.extractor.Param
import ro.pippo.core.HttpConstants
import javax.inject.Inject

@Path("/transfers")
class TransferController @Inject constructor( private val service: TransferService) : Controller() {

    @GET
    @Produces(Produces.JSON)
    @NoCache
    fun getAll(): ApiResponse<List<Transfer>> {
        val status = routeContext.getParameter("status").toString(null)?.let { valueOf(it) }
        return ApiResponse(returnObject = service.getAll(status))
    }

    @GET("/{id}/status")
    @Produces(Produces.JSON)
    @NoCache
    fun get(@Param id: Long) = service.getStatus(id)?.let {
        ApiResponse(returnObject = createTransferResponse(it, id))
    } ?: ApiResponse.notFound("Transfer doesn't exist").also { response.status(STATUS_NOT_FOUND) }

    @POST
    @Produces(Produces.JSON)
    @Consumes(Consumes.JSON)
    fun create(@Body request: TransferRequest) = service.create(request).let {
        response.status(STATUS_ACCEPTED)
        response.header(HttpConstants.Header.LOCATION, "${getRequest().applicationPath}/$it/status")
        ApiResponse<Void>()
    }

    @DELETE("/{id}")
    @Produces(Produces.JSON)
    fun cancel(@Param id: Long) = service.cancel(id)?.let {
        when (it) {
            FAILED, COMPLETED -> ApiResponse.notCancellable("Transfer cannot be cancelled", it).also {
                response.status(STATUS_BAD_REQUEST)
            }
            else -> ApiResponse(returnObject = createTransferResponse(it, id)).also { response.status(STATUS_ACCEPTED) }
        }
    } ?: ApiResponse.notFound("Transfer doesn't exist").also { response.status(STATUS_NOT_FOUND) }

    private fun createTransferResponse(
            status: TransferStatus,
            id: Long, cancellable:
            Boolean = false
    ): TransferResponse {
        val checkURI = "${getRequest().applicationPath}/$id}"
        return TransferResponse(
            id = id,
            status = status,
            checkURI = checkURI,
            cancelURI = if (cancellable) checkURI else null)
    }
}