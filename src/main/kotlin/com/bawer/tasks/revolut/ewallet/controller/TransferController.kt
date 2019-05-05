package com.bawer.tasks.revolut.ewallet.controller

import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.TransferStatus.*
import com.bawer.tasks.revolut.ewallet.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.response.ApiResponse
import com.bawer.tasks.revolut.ewallet.response.TransferResponse
import com.bawer.tasks.revolut.ewallet.service.TransferService
import ro.pippo.controller.*
import ro.pippo.controller.extractor.Body
import ro.pippo.controller.extractor.Param
import javax.inject.Inject

@Path("/transfers")
class TransferController(@Inject private val service: TransferService) : Controller() {

    @GET
    @Produces(Produces.JSON)
    @NoCache
    fun getAll() {
        val status = routeContext.getParameter("status").toString(null)?.let { TransferStatus.valueOf(it) }
        ApiResponse(returnObject = service.getAll(status))
    }

    @GET("/{id}")
    @Produces(Produces.JSON)
    @NoCache
    fun get(@Param id: Long) = service.get(id)?.let {
        ApiResponse(returnObject = createTransferResponse(it, id))
    } ?: ApiResponse.notFound("Transfer doesn't exist").also { response.status(404) }

    @POST
    @Produces(Produces.JSON)
    @Consumes(Consumes.JSON)
    fun create(@Body request: TransferRequest) = ApiResponse(returnObject = service.create(request)).also {
        response.status(202)
    }

    @DELETE("/{id}")
    @Produces(Produces.JSON)
    fun cancel(@Param id: Long) = service.cancel(id)?.let {
        when (it) {
            FAILED, COMPLETED -> ApiResponse.notCancellable("Transfer cannot be cancelled").also {
                response.status(405)
            }
            else -> ApiResponse(returnObject = createTransferResponse(it, id)).also { response.status(202) }
        }
    } ?: ApiResponse.notFound("Transfer doesn't exist").also { response.status(404) }

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