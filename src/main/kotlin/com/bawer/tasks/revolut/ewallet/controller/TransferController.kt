package com.bawer.tasks.revolut.ewallet.controller

import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.response.ApiResponse
import com.bawer.tasks.revolut.ewallet.service.TransferService
import ro.pippo.controller.*
import ro.pippo.controller.extractor.Body
import ro.pippo.controller.extractor.Param

@Path("/transfers")
class TransferController (
        private val service: TransferService
) : Controller() {

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
    fun get(@Param id: Int) = ApiResponse(returnObject = service.get(id))

    @POST
    @Produces(Produces.JSON)
    @Consumes(Consumes.JSON)
    fun create(@Body request: TransferRequest) = ApiResponse(returnObject = service.create(request)).also {
        response.status(202)
    }

    @DELETE("/{id}")
    @Produces(Produces.JSON)
    fun cancel(@Param id: Int) = ApiResponse(returnObject = service.cancel(id)).also {
        response.status(202)
    }
}