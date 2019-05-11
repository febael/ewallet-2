package com.bawer.tasks.revolut.ewallet.controller

import com.bawer.tasks.revolut.ewallet.STATUS_CREATED
import com.bawer.tasks.revolut.ewallet.STATUS_NOT_FOUND
import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.request.AccountRequest
import com.bawer.tasks.revolut.ewallet.model.request.TransferDirection
import com.bawer.tasks.revolut.ewallet.model.response.ApiResponse
import com.bawer.tasks.revolut.ewallet.service.AccountService
import ro.pippo.controller.*
import ro.pippo.controller.extractor.Body
import ro.pippo.controller.extractor.Param
import ro.pippo.core.HttpConstants
import javax.inject.Inject

@Path("/accounts")
class AccountController @Inject constructor(private val service: AccountService) : Controller() {

    @GET
    @Produces(Produces.JSON)
    @NoCache
    fun getAll() = ApiResponse(returnObject = service.getAll())

    @GET("/{id}")
    @Produces(Produces.JSON)
    @NoCache
    fun get(@Param id: Int) = service.get(id)?.let { ApiResponse(it) }
            ?: ApiResponse.notFound().also { response.status(STATUS_NOT_FOUND) }

    @GET("/{id}/transfers")
    @Produces(Produces.JSON)
    @NoCache
    fun getTransfers(@Param id: Int): ApiResponse<List<Transfer>> {
        val direction = routeContext.getParameter("direction").toString(null)?.let {
            TransferDirection.valueOf(it)
        } ?: TransferDirection.ALL
        val (limit, after) = getPagingParameters()
        return ApiResponse(returnObject = service.getTransfers(id, direction, limit, after))
    }

    @POST
    @Produces(Produces.JSON)
    @Consumes(Consumes.JSON)
    fun create(@Body request: AccountRequest) = ApiResponse(returnObject = service.create(request)).also {
        response.status(STATUS_CREATED)
        response.header(HttpConstants.Header.LOCATION, "${getRequest().applicationPath}/${it.returnObject!!.id}")
    }
}