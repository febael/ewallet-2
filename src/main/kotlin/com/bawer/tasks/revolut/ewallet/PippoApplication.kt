package com.bawer.tasks.revolut.ewallet

import com.bawer.tasks.revolut.ewallet.model.exception.EWalletException
import com.bawer.tasks.revolut.ewallet.model.response.ApiResponse
import com.google.gson.JsonSyntaxException
import ro.pippo.controller.Controller
import ro.pippo.controller.ControllerApplication
import ro.pippo.core.HttpConstants
import ro.pippo.core.route.RouteContext
import ro.pippo.gson.GsonEngine


class PippoApplicaton(private vararg val controllers: Controller) : ControllerApplication() {

    override fun onInit() {
        addControllers(*controllers)
        registerContentTypeEngine(GsonEngine::class.java)
        errorHandler.run {
            setExceptionHandler(EWalletException::class.java) { exception: Exception, context: RouteContext ->
                exception as EWalletException
                with (context) {
                    status(exception.status)
                    response.contentType(HttpConstants.ContentType.APPLICATION_JSON)
                    send(ApiResponse.failed(exception.message))
                }
            }
            setExceptionHandler(JsonSyntaxException::class.java) { exception: Exception, context: RouteContext ->
                with (context) {
                    status(STATUS_BAD_REQUEST)
                    response.contentType(HttpConstants.ContentType.APPLICATION_JSON)
                    send(ApiResponse.failed(exception.message))
                }
            }
            setExceptionHandler(Exception::class.java) { exception: Exception, context: RouteContext ->
                with (context) {
                    status(STATUS_INTERNAL_SERVER_ERROR)
                    response.contentType(HttpConstants.ContentType.APPLICATION_JSON)
                    send(ApiResponse.failed(exception.message))
                }
            }
        }
    }
}